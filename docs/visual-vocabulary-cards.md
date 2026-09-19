# Визуальные карточки: редакторские правила

## Критерий включения

Карточка добавляется только после ручной проверки конкретной словарной статьи. Иллюстрация должна без перевода вызывать одну доминирующую ассоциацию. Часть речи сама по себе не является критерием включения.

Первая подборка ограничена конкретными существительными. Пилот:

| `wordId` | Литовское слово | Объект |
| --- | --- | --- |
| `word_000145` | `katė` | кошка |
| `word_000146` | `šuo` | собака |
| `word_000277` | `obuolys` | яблоко |
| `word_000396` | `traukinys` | поезд |

Абстрактные понятия, служебные слова и сцены с несколькими равноправными толкованиями не включаются. Наглядные глаголы и признаки можно добавить позднее после такой же ручной проверки.

## Art guide

- квадратная иллюстрация без текста и элементов интерфейса;
- один крупный и однозначный главный объект;
- оригинальная плоская аппликация из фактурной бумаги;
- простые геометрические формы и толстый немного неровный тёмный контур;
- приглушённая холодная основа с охрой, кирпично-красным, тёмно-коричневым и тёплым белым;
- сухая взрослая сатирическая интонация через выражение или один второстепенный реквизит;
- запрещены существующие персонажи, логотипы, водяные знаки и детские буквенные украшения.

## Шаблон генерации

```text
Use case: illustration-story
Asset type: square raster illustration for an adult Lithuanian vocabulary card
Primary request: Create a memorable, instantly recognizable illustration of <OBJECT>.
Scene/backdrop: simple muted paper backdrop with at most one secondary prop
Subject: one original <OBJECT>, centered, large in frame
Style/medium: original crude flat cut-paper collage, layered construction-paper texture, simple geometric shapes, thick slightly uneven black outlines, deliberately stiff pose, deadpan adult satirical animation energy
Composition/framing: square, clean silhouette, generous safe margins, no UI frame
Color palette: muted icy blue, ochre, brick red, dark brown, warm white, black
Constraints: no text, no letters, no captions, no logos, no watermark; no existing copyrighted characters; production-ready mobile asset
Avoid: photorealism, preschool alphabet decorations, busy background
```

Литовское слово не встраивается в изображение: приложение выводит его отдельным Compose-текстом.

## Массовая партия 01

Первая подтверждённая массовая партия содержит 50 вручную отобранных статей. Все они обозначают один предмет с устойчивым силуэтом; близкие агрегаты, абстракции и сцены исключены.

| Категория | `wordId` и литовское слово |
| --- | --- |
| Еда | `word_000190` pienas; `word_000194` sūris; `word_000197` duona; `word_000209` žuvis; `word_000213` arbata; `word_000214` kava; `word_000218` medus; `word_000221` spurga; `word_000224` kiaušinis; `word_000259` agurkas; `word_000260` baklažanas; `word_000262` bulvė; `word_000264` česnakas; `word_000265` kopūstas; `word_000266` moliūgas; `word_000267` morka; `word_000268` pomidoras; `word_000273` apelsinas; `word_000274` bananas; `word_000275` citrina |
| Транспорт | `word_000389` automobilis; `word_000391` dviratis; `word_000392` paspirtukas; `word_000393` autobusas; `word_000401` lėktuvas; `word_000405` troleibusas |
| Дом | `word_000503` laiptai; `word_000505` liftas; `word_000506` balkonas; `word_000510` durys; `word_000513` langas; `word_000528` stalas; `word_000529` kėdė; `word_000530` lova; `word_000531` sofa; `word_000552` šaldytuvas; `word_000559` virdulys; `word_000561` dulkių siurblys |
| Одежда | `word_000602` megztinis; `word_000603` kelnės; `word_000604` sijonas; `word_000605` suknelė; `word_000610` marškinėliai; `word_000613` striukė; `word_000615` kojinės; `word_000618` šalikas |
| Аксессуары | `word_000667` kuprinė; `word_000668` skėtis; `word_000670` laikrodis; `word_000671` akiniai |
