# Airport Passenger Boarding System

A responsive front-end demonstration of the Airport Passenger Boarding System described in the project abstract.

## Features
- Add normal passengers to a FIFO queue.
- Add priority passengers with priority values from 1 to 4.
- Smaller priority values are boarded first.
- Priority passengers are boarded before normal passengers.
- If no priority passenger is waiting, the first passenger in the normal queue boards.
- Live queue counts, next passenger preview, and boarding history.
- Duplicate passenger IDs are prevented while passengers are waiting.
- Responsive airport-themed dashboard.

## Files
- `index.html` — page structure
- `style.css` — layout and styling
- `script.js` — queue logic and user interactions

## How to run
1. Download or extract the project folder.
2. Keep all three files (`index.html`, `style.css`, `script.js`) in the same folder.
3. Double-click `index.html` to open it in a modern web browser.
4. Enter a passenger name and ID, choose normal or priority passenger, and click **Add to queue**.
5. Click **Board next passenger** to process the next person.
6. Use **Reset demo** to clear the queues and boarding history.

No installation, server, database, or API key is required. An internet connection is only needed to load the optional Google Fonts; the page otherwise works offline using fallback fonts.

## Important note
This is a browser-based demonstration of the data-structure behaviour from the abstract. It uses JavaScript in the webpage for interactivity; it is not connected to an airport or a real flight system, and it does not execute Java backend code.
