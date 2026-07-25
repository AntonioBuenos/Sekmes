import { mkdir, readFile, writeFile } from "node:fs/promises";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repositoryRoot = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const defaultSourcePath = resolve(repositoryRoot, "tools/source/constitution_lt_ru_parallel.md");
const defaultOutputPath = resolve(repositoryRoot, "app/src/main/assets/content/constitution.json");

const blocks = [
    {
        id: "constitution-01",
        order: 1,
        title: "Основы государства",
        description: "Суверенитет, территория, гражданство, государственный язык и символы Литвы.",
        articleStart: 1,
        articleEnd: 17,
        includesPreamble: true,
    },
    {
        id: "constitution-02",
        order: 2,
        title: "Права, свободы и обязанности человека",
        description: "Достоинство, равенство, личные свободы, суд, выборы и объединения.",
        articleStart: 18,
        articleEnd: 37,
    },
    {
        id: "constitution-03",
        order: 3,
        title: "Семья, образование, культура, религия и СМИ",
        description: "Семья, дети, образование, культурная жизнь, вера и свобода информации.",
        articleStart: 38,
        articleEnd: 45,
    },
    {
        id: "constitution-04",
        order: 4,
        title: "Экономика, труд и социальная защита",
        description: "Собственность, работа, отдых, социальная помощь, здоровье и природа.",
        articleStart: 46,
        articleEnd: 54,
    },
    {
        id: "constitution-05",
        order: 5,
        title: "Сейм",
        description: "Выборы, состав, полномочия и порядок работы парламента.",
        articleStart: 55,
        articleEnd: 76,
    },
    {
        id: "constitution-06",
        order: 6,
        title: "Президент Республики",
        description: "Выборы, срок полномочий, функции и замещение Президента.",
        articleStart: 77,
        articleEnd: 90,
    },
    {
        id: "constitution-07",
        order: 7,
        title: "Правительство",
        description: "Формирование, программа, полномочия и ответственность Правительства.",
        articleStart: 91,
        articleEnd: 101,
    },
    {
        id: "constitution-08",
        order: 8,
        title: "Конституционный Суд, суды и прокуратура",
        description: "Конституционный контроль, правосудие, независимость судей и прокуратура.",
        articleStart: 102,
        articleEnd: 118,
    },
    {
        id: "constitution-09",
        order: 9,
        title: "Самоуправление, бюджет, контроль и Банк Литвы",
        description: "Местная власть, финансы государства, аудит и центральный банк.",
        articleStart: 119,
        articleEnd: 133,
    },
    {
        id: "constitution-10",
        order: 10,
        title: "Внешняя политика, оборона и чрезвычайные режимы",
        description: "Международные отношения, защита государства, война и чрезвычайное положение.",
        articleStart: 134,
        articleEnd: 146,
    },
    {
        id: "constitution-11",
        order: 11,
        title: "Изменение Конституции и заключительные положения",
        description: "Поправки, референдум и вступление Конституции в силу.",
        articleStart: 147,
        articleEnd: 154,
    },
];

function parseTableRow(line) {
    if (!line.startsWith("|")) return null;
    const cells = line.split("|").slice(1, -1).map((cell) => cell.trim());
    return cells.length === 3 ? cells : null;
}

function normalizeMarkdownText(value) {
    return value.replaceAll("**", "").trim();
}

function splitNumberedList(text) {
    const marker = /(?:^|\s)(\d+)\)\s*/g;
    const matches = [...text.matchAll(marker)];
    if (matches.length < 2 || matches[0].index === undefined) return null;

    const prefix = text.slice(0, matches[0].index).trim();
    return matches.map((match, index) => {
        const start = match.index + match[0].length;
        const end = index + 1 < matches.length ? matches[index + 1].index : text.length;
        const number = Number(match[1]);
        const body = text.slice(start, end).trim().replace(/[;,.]+$/, "");
        return {
            number,
            text: `${prefix ? `${prefix} ` : ""}${number}) ${body}`.trim(),
        };
    });
}

function numericFragments(text) {
    const fragments = [];
    const fractionPattern = /\b\d+\/\d+\b/g;
    const numberPattern = /\b\d+(?:[.,]\d+)?\b/g;

    for (const match of text.matchAll(fractionPattern)) {
        fragments.push({ start: match.index, end: match.index + match[0].length, type: "fraction" });
    }
    for (const match of text.matchAll(numberPattern)) {
        const start = match.index;
        const end = start + match[0].length;
        if (!fragments.some((fragment) => start >= fragment.start && end <= fragment.end)) {
            fragments.push({ start, end, type: "number" });
        }
    }
    return fragments.sort((left, right) => left.start - right.start);
}

function expandPart(articleNumber, sourcePartId, lt, ru) {
    const ltItems = splitNumberedList(lt);
    const ruItems = splitNumberedList(ru);
    if (ltItems === null && ruItems === null) {
        return [{
            id: sourcePartId,
            sourcePartId,
            itemNumber: null,
            lt,
            ru,
            ltNumericFragments: numericFragments(lt),
            ruNumericFragments: numericFragments(ru),
        }];
    }
    if (ltItems === null || ruItems === null || ltItems.length !== ruItems.length) {
        throw new Error(`Article ${articleNumber}, part ${sourcePartId}: numbered lists do not align`);
    }
    return ltItems.map((ltItem, index) => {
        const ruItem = ruItems[index];
        if (ltItem.number !== ruItem.number) {
            throw new Error(`Article ${articleNumber}, part ${sourcePartId}: item numbers do not align`);
        }
        return {
            id: `${sourcePartId}.${ltItem.number}`,
            sourcePartId,
            itemNumber: ltItem.number,
            lt: ltItem.text,
            ru: ruItem.text,
            ltNumericFragments: numericFragments(ltItem.text),
            ruNumericFragments: numericFragments(ruItem.text),
        };
    });
}

function articleBlockId(articleNumber) {
    const block = blocks.find((candidate) => articleNumber >= candidate.articleStart && articleNumber <= candidate.articleEnd);
    if (!block) throw new Error(`Article ${articleNumber} is not assigned to a learning block`);
    return block.id;
}

export function parseConstitutionMarkdown(markdown) {
    const articles = [];
    const articleByNumber = new Map();
    let currentSection = null;
    let currentArticle = null;
    let preambleParts = [];

    for (const line of markdown.split(/\r?\n/)) {
        if (line.startsWith("## ")) {
            currentSection = line.slice(3).trim();
            currentArticle = null;
            continue;
        }

        const row = parseTableRow(line);
        if (row === null) continue;
        const [rawId, rawLt, rawRu] = row;
        if (rawId === "№" || rawId.match(/^-+$/)) continue;
        const lt = normalizeMarkdownText(rawLt);
        const ru = normalizeMarkdownText(rawRu);

        if (/^P\d+$/.test(rawId)) {
            if (currentSection !== "Преамбула") {
                throw new Error(`Preamble row ${rawId} is outside the preamble`);
            }
            preambleParts.push({
                id: rawId.toLowerCase(),
                sourcePartId: rawId,
                itemNumber: null,
                lt,
                ru,
                ltNumericFragments: numericFragments(lt),
                ruNumericFragments: numericFragments(ru),
            });
            continue;
        }

        const articleHeader = rawId.match(/^\d+$/);
        if (articleHeader) {
            const number = Number(rawId);
            if (articleByNumber.has(number)) throw new Error(`Article ${number} is duplicated`);
            currentArticle = {
                id: `article-${number}`,
                number,
                blockId: articleBlockId(number),
                sectionTitle: currentSection,
                titleLt: lt,
                titleRu: ru,
                parts: [],
            };
            articles.push(currentArticle);
            articleByNumber.set(number, currentArticle);
            continue;
        }

        const partMatch = rawId.match(/^(\d+)\.(\d+)$/);
        if (partMatch) {
            const number = Number(partMatch[1]);
            if (currentArticle?.number !== number) {
                throw new Error(`Part ${rawId} does not follow article ${number}`);
            }
            currentArticle.parts.push(...expandPart(number, rawId, lt, ru));
        }
    }

    if (preambleParts.length !== 10) {
        throw new Error(`Expected 10 preamble parts, received ${preambleParts.length}`);
    }
    for (let number = 1; number <= 154; number += 1) {
        if (!articleByNumber.has(number)) throw new Error(`Article ${number} is missing`);
    }
    if (articles.length !== 154) throw new Error(`Expected 154 articles, received ${articles.length}`);
    if (articles.some((article) => article.parts.length === 0)) {
        throw new Error("Every article must contain at least one semantic part");
    }

    const document = {
        schemaVersion: 1,
        title: "Konstitucija / Конституция Литовской Республики",
        preamble: {
            id: "preamble",
            blockId: "constitution-01",
            titleLt: "PREAMBULĖ",
            titleRu: "ПРЕАМБУЛА",
            parts: preambleParts,
        },
        blocks: blocks.map((block) => ({
            ...block,
            articleIds: [
                ...(block.includesPreamble ? ["preamble"] : []),
                ...articles
                    .filter((article) => article.blockId === block.id)
                    .map((article) => article.id),
            ],
        })),
        articles,
    };

    const coveredArticleIds = document.blocks.flatMap((block) => block.articleIds.filter((id) => id !== "preamble"));
    if (new Set(coveredArticleIds).size !== 154 || coveredArticleIds.length !== 154) {
        throw new Error("Learning blocks must cover each article exactly once");
    }
    return document;
}

export async function importConstitution(sourcePath = defaultSourcePath, outputPath = defaultOutputPath) {
    const markdown = await readFile(sourcePath, "utf8");
    const document = parseConstitutionMarkdown(markdown);
    await mkdir(dirname(outputPath), { recursive: true });
    await writeFile(outputPath, `${JSON.stringify(document, null, 2)}\n`, "utf8");
    return document;
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const sourcePath = process.argv[2] ? resolve(process.argv[2]) : defaultSourcePath;
    const outputPath = process.argv[3] ? resolve(process.argv[3]) : defaultOutputPath;
    const document = await importConstitution(sourcePath, outputPath);
    const partCount = document.articles.reduce((total, article) => total + article.parts.length, document.preamble.parts.length);
    process.stdout.write(`Imported ${document.articles.length} articles, ${partCount} cards and ${document.blocks.length} blocks.\n`);
}
