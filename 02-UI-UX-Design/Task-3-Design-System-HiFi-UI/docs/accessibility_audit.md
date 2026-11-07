# Accessibility audit — ShiftLoop (WCAG 2.2 AA)

Contrast ratios are **computed** from the design tokens (relative luminance formula, WCAG 2.x).

| Theme | Usage | Foreground | Background | Ratio | Result |
|---|---|---|---|---|---|
| light | Body text on background | `#14181F` | `#F6F7F9` | 16.6:1 | AAA |
| light | Body text on surface | `#14181F` | `#FFFFFF` | 17.79:1 | AAA |
| light | Secondary text on surface | `#4A5263` | `#FFFFFF` | 7.84:1 | AAA |
| light | Primary button label | `#FFFFFF` | `#0A1F44` | 16.25:1 | AAA |
| light | Primary colour as text/icon | `#0A1F44` | `#FFFFFF` | 16.25:1 | AAA |
| light | Link / eyebrow (accent-strong) on surface | `#8A6D0B` | `#FFFFFF` | 4.91:1 | AA |
| light | Gold accent as TEXT on surface | `#C9A227` | `#FFFFFF` | 2.42:1 | FAIL |
| light | Text on gold quick-action | `#0A1F44` | `#C9A227` | 6.72:1 | AA |
| light | Error text on error container | `#B91C1C` | `#FEE2E2` | 5.3:1 | AA |
| light | Warning text on warning container | `#B45309` | `#FEF3C7` | 4.51:1 | AA |
| light | Success text on success container | `#15803D` | `#DCFCE7` | 4.57:1 | AA |
| light | Info text on info container | `#0369A1` | `#E0F2FE` | 5.17:1 | AA |
| light | Selected nav label on accent container | `#3A2E00` | `#F6EBC2` | 11.19:1 | AAA |
| light | Input border (non-text, 3:1) | `#4A5263` | `#FFFFFF` | 7.84:1 | AA |
| dark | Body text on background | `#E6E9EF` | `#0D1117` | 15.56:1 | AAA |
| dark | Body text on surface | `#E6E9EF` | `#161B24` | 14.2:1 | AAA |
| dark | Secondary text on surface | `#A9B1C1` | `#161B24` | 8.01:1 | AAA |
| dark | Primary button label | `#0A1F44` | `#B9C8EE` | 9.72:1 | AAA |
| dark | Primary colour as text/icon | `#B9C8EE` | `#161B24` | 10.33:1 | AAA |
| dark | Link / eyebrow (accent-strong) on surface | `#E4C24F` | `#161B24` | 9.97:1 | AAA |
| dark | Gold accent as TEXT on surface | `#E4C24F` | `#161B24` | 9.97:1 | AAA |
| dark | Text on gold quick-action | `#0A1F44` | `#E4C24F` | 9.38:1 | AAA |
| dark | Error text on error container | `#F87171` | `#3B1414` | 5.86:1 | AA |
| dark | Warning text on warning container | `#FBBF24` | `#3A2A05` | 8.31:1 | AAA |
| dark | Success text on success container | `#4ADE80` | `#12301F` | 8.21:1 | AAA |
| dark | Info text on info container | `#38BDF8` | `#0B2A3D` | 6.94:1 | AA |
| dark | Selected nav label on accent container | `#F6EBC2` | `#5A4700` | 7.53:1 | AAA |
| dark | Input border (non-text, 3:1) | `#A9B1C1` | `#161B24` | 8.01:1 | AA |

**1 pair(s) fail** — light: Gold accent as TEXT on surface. Mitigation below.

## Findings & fixes

| Issue | Guideline | Resolution |
|---|---|---|
| Gold #C9A227 as text on white | 2.3:1 — fails AA | Gold is decorative only (rings, progress, active pill). Text links use accent-strong #8A6D0B (5.0:1). |
| 10–11 px micro-labels (summary grid, timestamps) | Below 12 px minimum in our type scale | Raise to Caption 12/16 in the next iteration; already 12 px in the component library. |
| Status shown by colour dots | Colour-only meaning (WCAG 1.4.1) | Every dot is paired with a text chip (High / Safety / Inbound / Done). |
| Icon-only buttons | Need accessible names | aria-label on back / search / settings buttons; icons are aria-hidden. |
| Touch targets | ≥ 48 × 48 dp (Material), ≥ 44 pt (Apple) | Buttons 48, icon buttons 48, list rows ≥ 56–64, inputs 52, OTP boxes 48 × 56 — all pass. |
| Dynamic type | Text must scale to 200% | Settings ▸ Large text (1.3×) and layouts use wrapping, no fixed heights on text containers. |
| Motion & time limits | Handover must not time out | Auto-save offline every change; no timeouts on forms. |
| Language | Tamil / Hindi / English users | Language switch in Profile; voice notes for low-literacy users. |
