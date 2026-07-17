---
name: karpathy-coding-principles
description: Apply when writing, reviewing, or refactoring code anywhere in this repo. A distillation of Andrej Karpathy's publicly-shared software and AI-assisted-coding philosophy — simplicity first, understand everything, small verifiable steps, no bloat. Use alongside the project skill (basekmp-architecture) for HOW to write, not WHERE.
---

# Coding principles (in the spirit of Karpathy)

A distillation of Andrej Karpathy's publicly-shared engineering philosophy, paraphrased — not a
verbatim document. These shape HOW code is written; the project skill defines WHERE things go.

## 1. Simplicity is the goal, not a nice-to-have
The best code is simple, minimal, and obvious. Prefer fewer lines, fewer abstractions, fewer
dependencies, fewer moving parts. When two solutions work, ship the smaller one. Complexity is the
enemy — every abstraction must earn its place. If a function, layer, or generic isn't pulling real
weight today, remove it.

## 2. Understand every line you ship
Never add code you cannot explain — especially AI-generated code. If you can't say exactly what a
block does and why it's needed, do not commit it. Read the surrounding code first; match what is
already there. Build a mental model before typing.

## 3. Small, incremental, verifiable steps
Make the smallest change that moves forward, verify it (build, run, test, read the output), then
repeat. Avoid giant diffs and sweeping rewrites. Each step should leave the code working. A long
chain of tiny correct steps beats one big leap you can't trust.

## 4. Get something working end to end first
Stand up a minimal end-to-end path (a "walking skeleton") before polishing any part. Make it correct
and connected first; optimize and generalize later, only where measurement says it matters.

## 5. Optimize for the reader
Code is read far more than it is written. Spend the effort on clear names, obvious control flow, and
small functions. Self-documenting structure beats explanation. (In this repo: no comments — the names
and shape must carry the meaning.)

## 6. Be paranoid; trust nothing, verify everything
Assume there are bugs. Run it. Check the actual output, not what you expect. Add a quick test for the
thing you just changed. "It compiles" is not "it works." When something looks too easy, look harder.

## 7. Resist abstraction and bloat (YAGNI)
Don't build for hypothetical futures. No speculative interfaces, options, or layers "in case." Solve
today's problem concretely; generalize only when a second real case appears. Delete dead code
aggressively — unused scaffolding is a liability, not an asset.

## 8. Watch the diff size, especially with AI help
AI makes it easy to balloon a codebase. Keep changes surgical and proportional to the task. Review
every line as if you wrote it. Fewer, tighter lines age better than a large generated pile.

## 9. Measure before you optimize
Don't guess at performance. Make it correct and simple first; profile/measure if it's actually slow.
Most "optimizations" add complexity for no real gain.

## Quick checklist before finishing a change
- Is this the simplest version that works? Can anything be deleted?
- Do I understand and can I explain every line?
- Did I actually run/test it and read the result?
- Is the diff as small as the task allows?
- No speculative abstractions, no dead code, no unnecessary dependencies.
