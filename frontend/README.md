# AuctionHub Frontend

React/Vite frontend for the Spring Boot auction-bidding backend.

## Stack

- React 19
- Vite
- React Router
- Axios
- STOMP.js
- Native WebSocket/STOMP endpoint from Spring Boot

## Backend expected

The frontend is configured for:

- REST: `http://localhost:8080`
- WebSocket: `ws://localhost:8080/ws`

Override these with `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
```

## Run

```bash
npm install
npm run dev
```

Production build:

```bash
npm run build
```

## REST contracts used

The frontend currently calls:

```text
POST /api/auth/login
POST /api/auth/register

GET  /api/users/me

GET  /api/auctions
GET  /api/auctions/{id}
POST /api/auctions
PUT  /api/auctions/{id}/start
PUT  /api/auctions/{id}/end

POST /api/bids/{auctionId}
GET  /api/bids/auction/{auctionId}
GET  /api/bids/my
```

WebSocket subscriptions:

```text
/topic/auction/{auctionId}
/topic/auction/{auctionId}/status
```

## Authentication

JWT is stored in localStorage under `auction_token` and automatically sent as:

```text
Authorization: Bearer <token>
```

The login response can expose the JWT under `token`, `accessToken`, `jwt`, or `access_token`. If a user object is returned, it is stored too; otherwise the frontend calls `/api/users/me`.

## Important integration note

The UI is complete and the WebSocket integration is implemented against the backend contract established in the project. If your exact controller mappings differ from the paths listed above, only the small service files under `src/services/` need endpoint adjustment; the UI does not need to be rewritten.

## Features

- Home page
- Auction listing/search/filter
- Auction details
- Live bid feed
- Live auction status events
- Countdown timer
- JWT login/register
- Protected routes
- Wallet/balance display
- Bid placement
- Bid history
- My bids
- My auctions
- Seller auction creation
- Profile
- Responsive dark UI
- Automatic WebSocket reconnect
- API error handling
