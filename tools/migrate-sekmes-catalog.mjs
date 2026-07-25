import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const readJson = (relativePath) =>
    JSON.parse(fs.readFileSync(path.join(root, relativePath), "utf8"));

const dictionary = readJson("app/src/main/assets/content/dictionary.json");
const course = readJson("app/src/main/assets/content/courses/sekmes.json");
const ids = new Set(dictionary.entries.map((entry) => entry.id));
const references = course.lessons.flatMap((lesson) => lesson.wordRefs);
const missingReferences = references.filter((reference) => !ids.has(reference.wordId));
const duplicateIds = dictionary.entries.length - ids.size;

if (dictionary.entries.length !== 1039 || course.lessons.length !== 21) {
    throw new Error("Unexpected Sekmes catalog size");
}
if (duplicateIds !== 0 || missingReferences.length !== 0) {
    throw new Error("Dictionary IDs or lesson references are invalid");
}

console.log(JSON.stringify({
    entries: dictionary.entries.length,
    lessons: course.lessons.length,
    lessonWordOccurrences: references.length,
    duplicateIds,
    missingReferences: missingReferences.length,
}, null, 2));
