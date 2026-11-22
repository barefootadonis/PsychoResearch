# PsychoResearch

PsychoResearch is a Java web app for discovering research in psychoinformatics, where psychology and computing meet. It uses OpenAlex to help readers find papers on how technology can help us understand human behaviour.

## What it does today

- Loads psychoinformatics papers when the homepage opens.
- Searches OpenAlex by keyword.
- Displays each paper’s title, authors, publication year and OpenAlex link.
- Shows up to 10 papers per page and generates page buttons based on the number of results.

## Built with

- Java 19 and its built-in HTTP server and HTTP client
- Gson for reading and writing JSON
- HTML, CSS and JavaScript
- OpenAlex API for research data

## Run locally

1. Open the project in IntelliJ IDEA and select JDK 19 or later.
2. Make sure Gson is available in the project.
3. Go to **Run → Edit Configurations** and add your OpenAlex API key as an environment variable named `OPENALEX_API_KEY`. Keep the key out of Git.
4. Set the working directory to the folder containing `index.html` and `style.css`, then run `Main`.
5. Open [localhost:8080](http://localhost:8080) in your browser.

## Things to add

- [ ] **Account system** — let users create an account and sign in.
- [ ] **Community paper submissions** — let users share their own research or add links to papers missing from OpenAlex. Label community submissions so readers know where each paper came from.
- [ ] **Contact community authors** — let readers message authors who share their work on PsychoResearch to ask questions or propose collaborations. Only authors with a PsychoResearch account can receive messages.
- [ ] **Favourites** — let users save papers to a personal reading list.
- [ ] **Expanded search and filters** — search community submissions alongside OpenAlex results and filter by publication year, topic or source.
