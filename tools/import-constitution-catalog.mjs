import { readFile, writeFile } from "node:fs/promises";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repositoryRoot = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const paths = {
    dictionary: resolve(repositoryRoot, "app/src/main/assets/content/dictionary.json"),
    courseIndex: resolve(repositoryRoot, "app/src/main/assets/content/index.json"),
    constitution: resolve(repositoryRoot, "app/src/main/assets/content/constitution.json"),
    terms: resolve(repositoryRoot, "tools/source/constitution_lt_ru_terms_100.json"),
    course: resolve(repositoryRoot, "app/src/main/assets/content/courses/constitution.json"),
};

const additionalSurfaceForms = new Map([
    ["visuomenė", ["visuomenės", "visuomenei"]],
    ["lygybė", ["lygybės", "lygybę", "lygios", "lygia"]],
    ["orumas", ["orumo", "orumą"]],
    ["nepriklausomybė", ["nepriklausomybės", "nepriklausomybę"]],
    ["valdžia", ["valdžios", "valdžią"]],
    ["valstybės valdžia", ["valstybės valdžios", "valstybės valdžią"]],
    ["įstaiga", ["įstaigos", "įstaigų", "įstaigose"]],
    ["pareigūnas, pareigūnė", ["pareigūnus", "pareigūnų"]],
    ["atstovas, atstovė", ["atstovus", "atstovų"]],
    ["rinkėjas, rinkėja", ["rinkėjai", "rinkėjų"]],
    ["rinkimų teisė", ["rinkimų teisę", "rinkimų teisės"]],
    ["teisingumas", ["teisingumą"]],
    ["savivalda", ["savivaldos", "savivaldą"]],
    ["savivaldybė", ["savivaldybės", "savivaldybių"]],
    ["savivaldybės taryba", ["savivaldybių tarybas", "savivaldybių tarybų"]],
    ["meras, merė", ["merus", "merų"]],
    ["mokestis", ["mokesčiai", "mokesčių", "mokestį"]],
    ["švietimas", ["švietimo", "švietimą"]],
    ["ginkluotosios pajėgos", ["ginkluotąsias pajėgas", "ginkluotųjų pajėgų"]],
    ["karo padėtis", ["karo padėtį", "karo padėties"]],
    ["nepaprastoji padėtis", ["nepaprastąją padėtį", "nepaprastosios padėties"]],
    ["valstybinis, valstybinė", ["valstybinė", "valstybinės", "valstybinio"]],
    ["visuomeninis, visuomeninė", ["visuomeninės", "visuomeninių"]],
    ["lygus, lygi", ["lygiai"]],
]);

function normalize(value) {
    return value.trim().toLocaleLowerCase("lt").replace(/\s+/g, " ");
}

function termForms(term) {
    return [...new Set([
        term.lit,
        ...term.lit.split(",").map((form) => form.trim()),
        ...(additionalSurfaceForms.get(term.lit) ?? []),
    ].filter(Boolean))];
}

function nextWordId(entries) {
    const highest = entries.reduce((max, entry) => {
        const match = /^word_(\d+)$/.exec(entry.id);
        return match ? Math.max(max, Number(match[1])) : max;
    }, 0);
    return highest + 1;
}

function hasLetter(value) {
    return value !== undefined && /\p{L}/u.test(value);
}

function findFormRanges(text, form) {
    const normalizedText = text.toLocaleLowerCase("lt");
    const normalizedForm = normalize(form);
    const ranges = [];
    let offset = 0;
    while (offset < normalizedText.length) {
        const start = normalizedText.indexOf(normalizedForm, offset);
        if (start < 0) break;
        const end = start + normalizedForm.length;
        if (!hasLetter(normalizedText[start - 1]) && !hasLetter(normalizedText[end])) {
            ranges.push({ start, end });
        }
        offset = start + normalizedForm.length;
    }
    return ranges;
}

function termLinksForText(text, terms) {
    const candidates = terms.flatMap((term) =>
        term.forms.flatMap((form) =>
            findFormRanges(text, form).map((range) => ({ ...range, wordId: term.wordId })),
        ),
    ).sort((left, right) =>
        left.start - right.start || (right.end - right.start) - (left.end - left.start) || left.wordId.localeCompare(right.wordId),
    );

    const selected = [];
    for (const candidate of candidates) {
        if (selected.every((link) => candidate.end <= link.start || candidate.start >= link.end)) {
            selected.push(candidate);
        }
    }
    return selected.sort((left, right) => left.start - right.start);
}

function allParts(document) {
    return [
        ...document.preamble.parts.map((part) => ({ part, blockId: document.preamble.blockId })),
        ...document.articles.flatMap((article) => article.parts.map((part) => ({ part, blockId: article.blockId }))),
    ];
}

export function mergeConstitutionCatalog({ dictionary, courseIndex, document, terms }) {
    const entries = [...dictionary.entries];
    const exactWordIds = new Map(entries.map((entry) => [
        `${normalize(entry.lt)}\u0000${normalize(entry.ru)}\u0000${entry.type}`,
        entry.id,
    ]));
    let generatedId = nextWordId(entries);
    const resolvedTerms = terms.map((term) => {
        const key = `${normalize(term.lit)}\u0000${normalize(term.ru)}\u0000${term.type}`;
        let wordId = exactWordIds.get(key);
        if (!wordId) {
            wordId = `word_${String(generatedId).padStart(6, "0")}`;
            generatedId += 1;
            entries.push({ id: wordId, ru: term.ru, lt: term.lit, type: term.type });
            exactWordIds.set(key, wordId);
        }
        return { ...term, wordId, forms: termForms(term) };
    });

    const parts = allParts(document);
    const usedWordIdsByBlock = new Map(document.blocks.map((block) => [block.id, new Set()]));
    for (const { part, blockId } of parts) {
        part.termLinks = termLinksForText(part.lt, resolvedTerms);
        part.termLinks.forEach((link) => usedWordIdsByBlock.get(blockId).add(link.wordId));
    }

    const course = {
        schemaVersion: 1,
        id: "constitution",
        title: "Конституция Литвы",
        lessons: document.blocks.map((block) => ({
            id: block.id,
            title: `${block.order}. ${block.title}`,
            order: block.order,
            wordRefs: resolvedTerms
                .filter((term) => usedWordIdsByBlock.get(block.id).has(term.wordId))
                .map((term) => ({ wordId: term.wordId })),
        })),
    };

    const unmatchedTerms = resolvedTerms.filter((term) => !parts.some(({ part }) =>
        part.termLinks.some((link) => link.wordId === term.wordId),
    ));
    if (unmatchedTerms.length > 0) {
        throw new Error(`Terms without text links: ${unmatchedTerms.map((term) => term.lit).join(", ")}`);
    }
    if (course.lessons.some((lesson) => lesson.wordRefs.length < 4)) {
        throw new Error("Every Constitution block needs at least four terms for the quiz");
    }

    return {
        dictionary: { ...dictionary, entries },
        courseIndex: {
            ...courseIndex,
            coursePaths: [...new Set([...courseIndex.coursePaths, "content/courses/constitution.json"])],
        },
        document,
        course,
        reusedCount: resolvedTerms.length - (generatedId - nextWordId(dictionary.entries)),
        newCount: generatedId - nextWordId(dictionary.entries),
        termCount: resolvedTerms.length,
    };
}

export async function importConstitutionCatalog() {
    const [dictionarySource, indexSource, documentSource, termsSource] = await Promise.all([
        readFile(paths.dictionary, "utf8"),
        readFile(paths.courseIndex, "utf8"),
        readFile(paths.constitution, "utf8"),
        readFile(paths.terms, "utf8"),
    ]);
    const result = mergeConstitutionCatalog({
        dictionary: JSON.parse(dictionarySource),
        courseIndex: JSON.parse(indexSource),
        document: JSON.parse(documentSource),
        terms: JSON.parse(termsSource),
    });
    await Promise.all([
        writeFile(paths.dictionary, `${JSON.stringify(result.dictionary, null, 2)}\n`, "utf8"),
        writeFile(paths.courseIndex, `${JSON.stringify(result.courseIndex, null, 2)}\n`, "utf8"),
        writeFile(paths.constitution, `${JSON.stringify(result.document, null, 2)}\n`, "utf8"),
        writeFile(paths.course, `${JSON.stringify(result.course, null, 2)}\n`, "utf8"),
    ]);
    return result;
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const result = await importConstitutionCatalog();
    process.stdout.write(`Imported ${result.termCount} Constitution terms: ${result.reusedCount} reused, ${result.newCount} new.\n`);
}
