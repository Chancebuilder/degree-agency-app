# degree-agency-app

Degree Agency is a demo web application for a study-abroad / university
admissions agency. Students can browse degree programs from partner
universities and submit an application, which is stored and shown in an
applications list.

Built with **Next.js 16** (App Router), **TypeScript**, and **Tailwind CSS 4**.

## Features

- Landing page with featured programs
- Browse all degree programs (`/programs`)
- Submit an application (`/apply`) via a JSON API (`POST /api/applications`)
- Review submitted applications (`/applications`)
- Applications are persisted to a file-backed JSON store in `data/`

## Getting started

Requirements: Node.js 22+ and npm.

```bash
npm install       # install dependencies
npm run dev       # start the dev server on http://localhost:3000
```

## Scripts

| Command | Description |
| --- | --- |
| `npm run dev` | Start the Next.js dev server (port 3000) |
| `npm run build` | Create a production build |
| `npm run start` | Run the production server |
| `npm run lint` | Run ESLint |

## API

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/api/programs` | List available degree programs |
| `GET` | `/api/applications` | List submitted applications |
| `POST` | `/api/applications` | Submit an application (`fullName`, `email`, `programId`, `message`) |

## Project structure

```
src/
  app/
    api/            # Route handlers (programs, applications)
    apply/          # Application form page
    applications/   # Submitted applications list
    programs/       # Program catalog
    page.tsx        # Landing page
  components/       # UI components
  lib/              # Program seed data + file-backed store
```
