import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const readJson = (relativePath) =>
    JSON.parse(fs.readFileSync(path.join(root, relativePath), "utf8"));

const dictionary = readJson("app/src/main/assets/content/dictionary.json");
const course = readJson("app/src/main/assets/content/courses/sekmes.json");
const ids = new Set(dictionary.entries.map((entry) => entry.id));
const entriesByIdentity = new Map();
for (const entry of dictionary.entries) {
    const identity = `${entry.lt}\u0000${entry.ru}\u0000${entry.type}`;
    entriesByIdentity.set(identity, (entriesByIdentity.get(identity) ?? 0) + 1);
}
const references = course.lessons.flatMap((lesson) => lesson.wordRefs);
const missingReferences = references.filter((reference) => !ids.has(reference.wordId));

console.log(JSON.stringify({
    entries: dictionary.entries.length,
    lessons: course.lessons.length,
    lessonWordOccurrences: references.length,
    duplicateWordIds: dictionary.entries.length - ids.size,
    duplicateDictionaryEntries: [...entriesByIdentity.values()].filter((count) => count > 1).length,
    missingReferences: missingReferences.length,
    entriesPerLesson: Object.fromEntries(course.lessons.map((lesson) => [lesson.id, lesson.wordRefs.length])),
}, null, 2));
