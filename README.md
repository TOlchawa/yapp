# Shared Memory Project

## Overview
The **Shared Memory Project** is an open-source tool designed to facilitate the easy sharing of information. Inspired by real-life challenges in remembering and organizing information (like beer preferences and sharing them with friends), this project aims to act as a "shared memory" system with mobile support for convenience.

This project is being developed as a showcase of skills and as a practical tool for personal use. It also serves as a way to enjoy the development process while leveraging the power of AI tools like ChatGPT and GitHub Copilot.

---

## Features
- **Information Sharing:** A centralized system for storing and sharing information.
- **Mobile-Friendly:** Designed with mobile applications in mind for ease of use.
- **Open Source:** Built with collaboration in mind, allowing contributions from others.
- **Learning-Oriented:** Created as a platform for experimenting with new technologies and gaining practical experience.

---

## Motivation
This project was born from the need for:
- Simplifying the sharing of occasional but useful information.
- Solving real-world problems while showcasing development skills.
- Creating something enjoyable and meaningful without commercial pressure.

## Tech Stack
- **Programming Language:** Java and JavaScript
- **Frameworks:** Spring Boot on the backend and React with Vite on the frontend
- **AI Tools:** ChatGPT, GitHub Copilot
- **Deployment:** [Your hosting solution, e.g., AWS, Netlify, Vercel]

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8.1+

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/TOlchawa/yapp
   ```

Run `scripts/setup-maven.sh` to install Maven if needed.

### Camera Preview
The web camera works only over HTTPS or on `localhost`.
Give the browser permission to use the camera.

### Code Formatting
Run the following command in `frontend` to format React files:

```bash
npm run format
```

## API Documentation
You can explore the REST API using the online Swagger UI:
[https://memoritta.com/swagger-ui/index.html](https://memoritta.com/swagger-ui/index.html)

To open the Swagger UI directly, visit https://memoritta.com/swagger-ui/index.html.

## Deployment with GitHub Actions

The project uses GitHub Actions to deploy the backend and frontend.

Workflows:

- `.github/workflows/deploy-prod.yml` runs when you create a `release/*` branch.
- `.github/workflows/deploy-on-merge.yml` runs when a branch named `release/*` is created.
- `.github/workflows/restart-services.yml` runs whenever code is pushed to `main`.

`deploy-on-merge.yml` starts when you create a `release/*` branch. It checks out the code and runs `scripts/restart-services.sh` to restart the services.

All workflows expect an environment called `PROD`. To configure it:

1. In your repository, open **Settings → Environments** and create `PROD`.
2. In `PROD` add these *secrets*:
   - `SSH_USER`
   - `SSH_PASSWORD`
   - `SSH_HOST`
3. Add these *variables*:
   - `SERVER_HOME`
   - `FRONTEND_HOME`
   - `SERVER_SCRIPT`
   - `FRONTEND_SCRIPT`

`restart-services.yml` reads these values and passes them to `scripts/restart-services.sh` to restart the services on your server whenever `main` is updated.
The script uses a lock file on the server so only one restart runs at a time.
If another run holds the lock, the script waits up to 30 seconds plus a random 10–30 seconds before giving up.

## Automating Releases

Run `scripts/release.sh` to tag a new version and create a GitHub release.
The script collects commit messages since the last tag, bumps the version
according to commit type, pushes the tag, and (if `gh` is installed) creates
a release with the changelog.

Make the script executable once with `chmod +x scripts/release.sh`.
