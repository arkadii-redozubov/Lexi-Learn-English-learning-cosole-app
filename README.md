# Lexi Learn

Lexi Learn is a Java command-line application for building English vocabulary. It combines a dictionary with short practice activities, then saves words and learning progress locally so they are available in later sessions.

## Features

- Look up English words and review phonetics, parts of speech, definitions, examples, synonyms, and antonyms.
- Browse vocabulary by topic (animals, food and drink, emotions, travel, and technology) or CEFR level (A1–C2).
- Open a random word from the saved vocabulary list.
- Practise with self-rated flashcards or multiple-choice quizzes.
- Track practice streaks, attempt counts, average scores, and best scores.
- Choose display themes, flashcard detail and length, quiz difficulty, and dictionary entry detail.

## How it works

The main menu in `Main` routes to dictionary, practice, progress, and settings features. `ApiService` requests word data from the [Free Dictionary API](https://dictionaryapi.dev/), parses the response into `Word` and `Meaning` objects, and falls back to `cache.json` when a request cannot reach the API. When a looked-up word is saved, the app stores its full dictionary response in the cache and a shorter version in the flashcard list.

`Exercises` runs flashcard and quiz sessions using `flashCards.json`. It writes each result to the relevant progress file. If there are not enough saved words, it can populate the list from the category or level vocabulary and fetch the required dictionary data. This initial fill requires an internet connection. Settings and progress are stored in local JSON files and are loaded again when the app starts.

## Requirements

- JDK 17 or later
- Internet access for live dictionary lookups and initial vocabulary loading

The project includes the `org.json` library at `lib/json-20250107.jar`. Run commands from the project root because the app reads and writes its JSON files relative to the current directory.

## Build and run

From a terminal in the project root:

```sh
mkdir -p bin
javac -cp "lib/json-20250107.jar" -d bin src/griffth/*.java
java -cp "bin:lib/json-20250107.jar" griffth.Main
```

On Windows, use `;` instead of `:` in the runtime classpath:

```bat
java -cp "bin;lib/json-20250107.jar" griffth.Main
```

The project can also be imported into Eclipse as an existing Java project; the included `.project` and `.classpath` files configure the source folder and JSON library.

## Local data files

- `category.json` and `level.json` contain vocabulary groups for browsing.
- `cache.json` stores full dictionary responses for offline lookup.
- `flashCards.json` stores the vocabulary used by flashcards, quizzes, and random-word selection.
- `flashCardsProgress.json` and `quizProgress.json` store practice results and streak data.
- `settings.json` stores the selected preferences.

These files are read and updated by the application while it runs.
