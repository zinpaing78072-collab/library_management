# FXML style standard — Library Management

Every screen under `src/main/resources/` follows the same shape. When adding or
editing a screen, copy the geometry from an existing one: `Category.fxml` for a
simple table+form screen, `Member.fxml` / `User.fxml` for large ones.

## File conventions

- XML declaration, blank line, `<?import?>` lines (controls first, layout last),
  blank line, root element.
- Namespace `http://javafx.com/javafx/21.0.6`; `fx:controller` on the root.
- 3-space indent per level, one element per line, attributes on a single line in
  order: `fx:id`, `layoutX`/`layoutY`, sizes (`prefHeight`/`prefWidth`),
  `spacing`, `mnemonicParsing`, `onAction`, `styleClass`, `text`.
- Root has a fixed size: `max/min/pref` all locked.

## Screen grid

- **Header row**: search/filter label with `styleClass="label-title"` at
  `(48, 40)`; its input at `(196, 35)` with `prefWidth="260"` (combo boxes use
  `prefWidth="187"`).
- **Content**: a single `<HBox layoutY="95">` with
  `prefHeight = root height − 95` so it fills to the bottom edge exactly.
  Children widths (table + form) must **sum to the root width** — no overflow,
  no dead strip.
- **Table**: column widths sized to their headers, headers in Title Case with
  spaces ("Date Of Birth", "Phone Number", "Book ID"). The column total must
  fit inside the table — no horizontal scrollbar; keep ~15px slack for the
  vertical scrollbar. **Never use exactly `prefWidth="80.0"`**: 80 is JavaFX's
  default column width, and `TableColumnHeader.updateScene` auto-sizes any
  column still at 80 to fit its content (first 30 rows) when it first attaches
  to a Scene — which silently blows the column total past the table width.
  Use 79, 81, 84… anything but 80. Use a 440-wide form panel when the screen
  has no image column (table = root width − 440).

## Form grid (all screens, including BookForm)

- Label column `layoutX="56"`, input column `layoutX="180"`,
  input `prefWidth="187"` (DatePickers also `prefHeight="31"`).
- Row pitch **44px**; after a multi-line control (TextArea) the next row starts
  at `control bottom + 44`.
- Label `layoutY = input layoutY + 5`.
- Buttons sit 44px below the last row: primary at `x=56`, secondary at `x=256`
  with `styleClass="button-secondary"`.
- Gender radio group: `<HBox prefHeight="38" spacing="30">`.

## Rules

- Never bind `layoutX`/`layoutY` in code for a node inside an `AnchorPane` —
  `AnchorPane.layoutChildren()` calls `resizeRelocate()` on every managed child
  and throws *"A bound value cannot be set"*. Bind `translateX`/`translateY`
  instead (see `LoginApp.fitToPane`).
- `fx:id` is camelCase and must match the `@FXML` field exactly
  (e.g. `colDescription`).
- Keep `Login.fxml`'s bespoke card geometry, but its file conventions above
  still apply.

## Validation

`/tmp/pulse-smoke/run.sh` loads every screen on a real Stage (layout pulses),
clicks through all dashboard buttons, LogOut (Login screen) and the admin path
(User screen), and asserts: no layout exceptions, every screen loads, nested
screens visually centered. Exit 0 = PASS.
