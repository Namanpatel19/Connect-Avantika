# Contributing to Avantika Connect

Thank you for your interest in contributing! This document outlines the workflow and conventions used by the development team.

## Branching Strategy

We follow a task-oriented branching strategy. All work is done in dedicated branches and merged into the main development line through Pull Requests.

- **Main Branches**: `main` (Production) and `develop` (Integration).
- **Work Branches**: Named after the feature or task being addressed. Examples from our history:
    - `push_notification_work`
    - `event_approval`
    - `additional_changes`

**Workflow:**
1. Create a branch named after your task (e.g., `feature/student-leaderboard` or `bugfix/login-error`).
2. Commit your changes locally.
3. Open a **Pull Request (PR)** to merge your branch into the target integration branch.
4. Once reviewed and approved, the PR is merged (e.g., Merge pull request #9).

## Commit Message Convention

To keep the project history clear and actionable, we use descriptive messages starting with an action verb.

**Common prefixes and patterns:**
- `implement`: For new features or functionalities (e.g., `implement PDF export for event registrations`)
- `update`: For enhancing or modifying existing logic (e.g., `update notification logic and profile update logic`)
- `fix`: For bug fixes and resolving errors (e.g., `fixing navigation issue of student`)
- `refactor`: For code cleanup without changing behavior (e.g., `refactor event creation and club management features`)
- `change`: For UI or layout modifications (e.g., `changing UI of login screen`)
- `cleanup`: For removing unused code or general maintenance.

**Examples from our repository:**
- `implement dean management, leaderboard functionality, and refine event registration logic`
- `push notification fixation and event club final fixes`
- `API failed error resolve`
- `layout change and app opening animation change`

## Pull Request (PR) Process

1. **Review**: All code must be reviewed via a Pull Request before being merged.
2. **Merging**: Merges are typically handled by a lead developer or the team member responsible for the integration.
3. **Documentation**: Ensure `README.md` or other relevant docs are updated if your changes affect setup or usage.
