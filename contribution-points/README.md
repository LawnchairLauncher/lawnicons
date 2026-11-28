# Contributor points

Contributors get points for new icons and icon updates. They can spend points to increase icon request priority on the Lawnicons dashboard.

[Lawnicons dashboard](https://lawnicons-requests.vercel.app/)

## Spending points

To increase the priority of icon requests, add their app IDs to the `spends` section of your points file.

1. Open the [Lawnicons dashboard](https://lawnicons-requests.vercel.app/).
2. Select the requests you want to prioritize.
3. In the selection bar menu, choose `Copy → app IDs`.
4. Open your `nickname.txt` file in [contribution-points/contributors](https://github.com/LawnchairLauncher/lawnicons/tree/develop/contribution-points/contributors).
5. Add one line per app ID under `spends` in this format: `appID points`.

The points must be `1`, `2`, `3`, `4`, `5`, or a multiple of `5` for higher priority. `earned`, `spent`, and `balance` are recalculated automatically. When a request is added to `appfilter.xml`, the workflow moves its line from `spends` to `fulfilled`.

## Example

`contribution-points/contributors/nickname.txt`

```text
earned 25
spent 25
balance 0

spends
app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity 5
app.lawnchair/app.lawnchair.LawnchairLauncher 10

fulfilled
# Filled automatically when the request is added to appfilter.xml
com.github.android/com.github.android.main.MainActivity 10

awards
# Filled automatically from merged PR titles
+5 points for pr 1234 with 1 icon
+20 points for pr 1240 with 4 updates
```
