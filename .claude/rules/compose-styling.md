---
paths:
  - "FloconDesktop/**/*.kt"
---

# Compose styling (`FloconDesktop/`)

How things look: colours, typography, spacing, shapes, icons. Structure and state rules are in `compose.md`.
All tokens come from `FloconTheme` (`library/designsystem/.../FloconTheme.kt`, values in `theme/`).

## Colours: `FloconTheme.colors`
- Only use `FloconColors` tokens. No `Color(0x…)`, `Color.White`/`Color.Black`, or `MaterialTheme.colorScheme` in feature code. `Color.Transparent` and `Color.Unspecified` are fine.
- Pick by role, not by look:

| Role | Token |
|---|---|
| App / page background (lowest) | `bg000` |
| Panels, cards, list containers | `bg100` |
| Raised surfaces: rows on hover, chips, inputs | `bg200` |
| Highest surface, scrollbar thumb | `bg300` |
| Dividers and borders | `line`; emphasised border: `lineStrong` |
| Primary text and icons | `ink` |
| Secondary text (labels, metadata) | `inkMuted` |
| Disabled / placeholder / hint | `inkFaint` |
| Brand highlight, selection, focused state | `accent`; tinted background: `accentSoft`; text on `accent`: `accentInk` |
| Success / info / warning / error | `statusSuccess` / `statusInfo` / `statusWarning` / `statusError` |
| HTTP code | `colors.httpStatus(code)`, never a hand-written `when` |
| Event category (network, database, analytics, crash, …) | `colors.category(EventCategory.X)`, which returns `(colour, softBackground)` |

- Prefer a dedicated token over `.copy(alpha = …)`: `inkMuted`/`inkFaint` rather than `ink.copy(alpha = 0.6f)`, `accentSoft` rather than `accent.copy(alpha = 0.1f)`. Use alpha only for one-off overlays with no matching token.
- Every colour must work in both light and dark themes (`FloconLightColors` / `FloconDarkColors`). Check previews in both. Use `FloconTheme.isDarkTheme` only when a design really has to differ, never to choose a raw colour.
- A new semantic colour goes into `FloconColors` with both a light and a dark value, not into a feature file.

## Typography: `FloconTheme.typography`
- Always set `style = FloconTheme.typography.xxx` on `Text`. Never set `fontSize = …sp` directly: the theme scales text with the user's app scale (`AppScale`), and a raw `sp` skips that.
- Default scale for a dense debugging UI:

| Use | Style |
|---|---|
| Table rows, list items, most UI text | `bodySmall` |
| Body text, empty states, dialog content | `bodyMedium` |
| Tags, chips, badges, column headers, timestamps | `labelSmall` / `labelMedium` |
| Section and panel titles | `titleSmall` / `titleMedium` |
| Page title (rare) | `titleLarge` |

- Adjust with `.copy(fontWeight = …)` or `.copy(fontFamily = FontFamily.Monospace)`. Use monospace for raw data: JSON, payloads, URLs, headers, SQL, ids.
- Long single-line text: `maxLines = 1, overflow = TextOverflow.Ellipsis`. Text the user may want to copy goes in a `SelectionContainer`.

## Spacing: `FloconTheme.spacing`
- Padding, `Arrangement.spacedBy` and `Spacer` sizes use the spacing tokens, which scale with `AppScale`:
  `tiny` 2, `extraSmall` 4, `compact` 6, `small` 8, `medium` 12, `large` 16, `extraLarge` 24, `doubleExtraLarge` 32, `tripleExtraLarge` 48, `huge` 64 (dp).
- Don't write `8.dp` where `FloconTheme.spacing.small` exists. Raw `dp` is only for borders (`1.dp`), icon sizes and fixed component dimensions.
- A fixed width or height that holds text (column widths, dropdown widths, buttons) is multiplied by `FloconTheme.scale` (`220.dp * FloconTheme.scale`) so it grows with the text.
- Usual rhythm: rows `vertical = small, horizontal = medium`; between sections `large`; inside a chip `horizontal = small, vertical = tiny`.

## Shapes & borders: `FloconTheme.shapes`
- Corners: `extraSmall` 2, `small` 4 (chips, inputs, buttons), `medium` 8 (cards, panels, list containers), `large` 16, `extraLarge` 32 (pills). No `RoundedCornerShape(...)` in feature code.
- Use the same shape for `clip`, `background` and `border` on one element. Order the modifiers `clip → background → border → clickable → padding`, so the ripple stays inside the shape and the padding inside the click area.
- Borders are `1.dp` with `colors.line` (or `lineStrong` for focus/emphasis).

## Icons
- Use `FloconIcon` / `FloconIconButton`, not raw `Icon`/`IconButton`. The default tint is `LocalContentColor`; pass `tint = FloconTheme.colors.inkMuted` for secondary icons.
- Use the `Icons.Outlined.*` set (`Icons.AutoMirrored.Outlined.*` for directional icons). Use `Filled` only for an active/selected state.
- Icon sizes: 14–16.dp inline with text, 20.dp in buttons and toolbars.

## States & interaction
- Hover: background goes one surface step up (`bg100` → `bg200`, `bg200` → `bg300`). Selected: `accent` border or indicator, optionally with an `accentSoft` background. Disabled: content in `inkFaint`.
- Use the theme ripple (`LocalIndication`, tinted with `accent`); don't create custom ripple colours.
- Prefer `FloconAnimateVisibility` over ad hoc `AnimatedVisibility` specs in new code.
