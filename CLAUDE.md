# Flocon

## Models

- **Planning a task: use Opus.** Analysis, design, architecture decisions, breaking a task into steps, and reviewing a plan are done with Opus.
- **Doing a task: use Sonnet.** Once a plan exists, implementing it (writing code, editing files, running checks, fixing CI, updating docs) is done with Sonnet.

When delegating to subagents, apply the same split: planning/exploration-for-design agents run on Opus, implementation agents run on Sonnet.
