# Station's AFK Button

A playful, visual RuneLite reminder for the moment a sustained skilling action stops.

After the configured activity and idle delays, the plugin dims the game and shows a large pixel-art emergency button. The first click dismisses the alert and is intentionally consumed so it cannot accidentally move your character; following clicks behave normally. Eight supplied sprite frames animate a fast, smooth physical button press without delaying subsequent input.

Hold Alt while the button is visible to drag or resize its RuneLite overlay. Its saved bounds preserve the pixel art's aspect ratio.

## Current detection

- Arms only after a supported gathering or production skill gains XP and its matching animation continues for the configured duration.
- Fires when that animation stops and the player remains idle for the configured grace period.
- Disarms on any mouse press, logout, movement, or unrelated animation such as a home teleport.
- Includes a configurable preview hotkey for testing the button and dismissal animation.

## Development

Using the included Gradle wrapper:

```powershell
.\gradlew.bat test
.\gradlew.bat run
```

The gentle alert chime and snappy arcade-button click can each be toggled independently. A shared volume slider defaults to 35%.
