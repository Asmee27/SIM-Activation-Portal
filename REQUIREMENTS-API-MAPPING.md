# SIM Activation Portal: Requirements-to-API Blueprint

This document is derived from:

- `SIMActivationPortal_UserStories.xlsx`, including the visible `User Stories` sheet and hidden `Backend` sheet.
- `sim-project-ui/web/WebContent/simhome.html`
- `sim-project-ui/web/WebContent/js/simhome.js`
- UI fixture files under `sim-project-ui/web/WebContent/*.json`

It is a requirements baseline, not an implementation decision record. Items marked **Unspecified** must be decided before the related phase is implemented.

## 1. Verified runtime and service facts

| Item | Evidence | Verified value |
|---|---|---|
| Browser base URL | `simhome.js` | `http://localhost:8181` |
| SIM API path | `simhome.js` | `/sim/validation` |
| Offer API path | `simhome.js` | `/sim/offer/{simNumber}` |
| Customer validation path | `simhome.js` | `/customer/validation` |
| ID proof path | `simhome.js` | `/idproof/validation` |
| Customer activation/details path | `simhome.js` | `/customer/activate` |
| Final activation path | `simhome.js` | `/sim/activation`, declared but not called by current UI |
| SIM service port | hidden `Backend` sheet | `5000` |
| Customer service port | hidden `Backend` sheet | `5001` |
| ID proof service port | hidden `Backend` sheet | `5002` |
| Eureka server port | repository configuration | `8761` |
| Config server port | repository configuration | `8888` |
| API gateway port | repository configuration and UI | `8181` |

The UI currently makes direct calls to the gateway URL. It does not contain a login screen, token acquisition call, `Authorization` header, or token storage flow.

## 2. End-to-end workflow mapping

| UI step | Workbook requirement | UI behavior | API operation | Capability involved | Backend owner |
|---|---|---|---|---|---|
| Brochureware to SIM details | Buyer can navigate from brochureware to SIM validation; `Get Connected` proceeds | Carousel advances | None | Portal presentation | UI |
| SIM and service details | SIM is 13-digit numeric; service number is 10-digit numeric; distinguish invalid and already-active SIM | Sends both values as JSON and advances on any truthy response | `POST /sim/validation` | SIM validation | SIM capability |
| Offer display | Show the special offer of the day | Separately requests offer after SIM submit and renders `offerName`, `callQty`, `dataQty`, `duration`, `cost` | `GET /sim/offer/{simNumber}` | Offer retrieval | Offer capability, initially possibly SIM service |
| Email and DOB validation | Both mandatory; validate existing activation request; return `No request placed for you.` when absent | Sends `email` and `dob`; advances on success | `POST /customer/validation` | Activation-request/customer validation | Customer capability |
| Personal details | First/last names alphabetic, max 15; confirm email must match; all fields mandatory | Confirm email is checked only in browser; no backend request | No current API call | Customer details | Customer capability |
| Address | Address, state, city, and six-digit PIN; state from Indian states; city excludes special characters except spaces | Address is stored locally; Google geocoding is called directly from browser | No current API call; data later included in `/customer/activate` | Address management | Customer capability |
| ID proof | Validate government ID, state, number, name, and DOB against existing details | Sends ID proof JSON after browser-side name/state checks | `POST /idproof/validation` | Identity verification | ID proof capability |
| Order summary | Display first name, last name, email, DOB, offer, and service number | Summary is rendered from `localStorage` | None | Summary/presentation | UI or orchestration read model |
| Activation confirmation | Confirm request and activate SIM; show success | Sends customer/address payload to `/customer/activate`; does not call declared `/sim/activation` | `POST /customer/activate`; final `/sim/activation` is not wired | Activation orchestration | **Unspecified** |

## 3. API contract observed from the UI

### `POST /sim/validation`

Request:

```json
{
  "simNumber": "1234567891234",
  "serviceNumber": "1234567891"
}
```

Observed frontend expectation: any truthy success response advances the carousel. The workbook requires these business outcomes:

- valid details: `Valid details. Welcome!!.`
- invalid details: `Invalid details, please check again SIM number/Service number!!.`
- already active: `SIM already active`

**Response shape:** Unspecified by workbook. Existing `MessageResponse` suggests `{ "message": "..." }`, but this must be confirmed as the API contract.

### `GET /sim/offer/{simNumber}`

Observed response fields:

```json
{
  "offerId": 5000,
  "offerName": "Special offer",
  "callQty": 100,
  "dataQty": 120,
  "duration": 10,
  "cost": 20
}
```

The UI requires all six fields. The fixture `offers.json` uses a different shape (`data`, `val`, `calls`, `cost`), so it is not an authoritative backend response schema.

### `POST /customer/validation`

Request:

```json
{
  "email": "a@b.c",
  "dob": "1990-12-12"
}
```

The HTML uses an HTML5 date input, so the current browser sends ISO-style `yyyy-MM-dd`, despite the workbook describing `dd/mm/yyyy`. The canonical API date format is **Unspecified** and must be selected before implementation.

Required outcomes:

- matching activation request: proceed
- no matching request: `No request placed for you.`
- invalid/in-use details: workbook says `Invalid details` in the relevant case

### `POST /idproof/validation`

Request observed in `simhome.js`:

```json
{
  "idType": "Aadhaar",
  "idNumber": "12345",
  "firstName": "Mihir",
  "lastName": "Khandekar",
  "state": "Maharashtra",
  "dob": "1990-12-12"
}
```

The backend must validate the supplied fields against its own identity data. `aadhaar.json` is only a UI fixture:

```json
[
  {
    "id": "12345",
    "fname": "Mihir",
    "lname": "Khandekar",
    "state": "Maharashtra"
  }
]
```

The fixture does not contain DOB or ID type, so those fields cannot be inferred from it.

### `POST /customer/activate`

Request observed in `simhome.js`:

```json
{
  "firstName": "Mihir",
  "lastName": "Khandekar",
  "email": "a@b.c",
  "dob": "1990-12-12",
  "address": {
    "address": "some address",
    "pinCode": "000000",
    "city": "Some City",
    "state": "Maharashtra"
  },
  "simNumber": "1234567891234"
}
```

The operation is named `customer/activate`, but the workbook describes this as request confirmation followed by SIM activation. Whether this endpoint creates a request, performs activation, or only updates customer details is **Unspecified**.

### `POST /sim/activation`

This URL is declared in the UI JavaScript but never invoked. The request body and response are therefore **Unspecified**. It must not be implemented based solely on the unused variable.

## 4. Data fixtures and their evidentiary status

| File | Observed data | Status |
|---|---|---|
| `simdetails.json` | SIM/service pairs and `active`/`inactive` status | Development fixture only; useful for seed data |
| `basicinfo.json` | `a@b.c`, `1990-12-12` | Development customer-validation fixture only |
| `aadhaar.json` | ID, first name, last name, state | Development identity fixture only; incomplete for full workbook validation |
| `offers.json` | Offer values using UI-oriented field names | Development display fixture only; conflicts with the Java `OfferDTO` shape |

## 5. Capability boundary proposed by evidence

These are capability boundaries, not yet final deployment boundaries:

1. **SIM capability**
   - SIM number/service-number validation.
   - SIM status.
   - SIM activation state transition.

2. **Offer capability**
   - Offer lookup and offer details.
   - May initially live in the SIM service because the current scaffold and UI route use `/sim/offer`.
   - Separate deployment is not justified by the current evidence.

3. **Customer and activation-request capability**
   - Email/DOB lookup.
   - Customer name and confirmation details.
   - Address update.
   - Activation-request lifecycle.

4. **Identity capability**
   - Government ID proof lookup and validation.

5. **Activation orchestration capability**
   - Coordinates the completed validations and the final state transition.
   - Whether this is a separate service or a component of an existing service remains **Unspecified**.
   - This is the first likely location where Feign has a real purpose.

## 6. Decisions (resolved 2026-11-04)

| # | Question | Decision |
|---|---|---|
| 1 | Date format | ISO `yyyy-MM-dd` everywhere (what the HTML5 date input sends); `LocalDate` in Java. |
| 2 | Activation ownership | `POST /customer/activate` saves the address, then calls `POST /sim/activation` to flip the SIM to `ACTIVE`. The UI never calls `/sim/activation` directly. |
| 3 | Orchestrator boundary | Orchestration lives in `customer-service`, using Feign (`SimClient`) to call `sim-service`. No separate activation service. |
| 4 | Offer ownership | Offers stay in `sim-service` (`/sim/offer/{simNumber}`). |
| 5 | Security | JWT: a login endpoint issues tokens; the gateway verifies them. The UI gets a small token-handling addition. *(Phase 7)* |
| 6 | Endpoint protection | All business endpoints need a token; `/auth/**` and `/actuator/health` stay open. *(Phase 7)* |
| 7 | ID proof schema | `id_proof` table: `id_number` (PK), `id_type`, `first_name`, `last_name`, `dob`, `state`. Every field must match (case-insensitive for text). |
| 8 | SIM state transition | Only on final confirmation (`/customer/activate` → `/sim/activation`). |
| 9 | Persistence | In-memory H2 per service, reseeded from `data.sql` on every start. |
| 10 | UI corrections | Allowed. The UI was fixed where it was broken (see section 8). |

Additional interpretations:

- **US04 "email already in use":** an email that exists but whose DOB differs → `400 Invalid details`; an unknown email → `404 No request placed for you.`
- **US04 email rule** (one `@`, one dot, then 2–3 letters) is enforced, so the old fixture `a@b.c` is rejected. Seed customers use `mihir@infy.com` / `priya@infy.com`.
- **US05 name check** is server-side via a new `POST /customer/details {email, firstName, lastName}`.
- **State list** = the 35 states/UTs in the UI dropdown (not strictly the workbook's "29 states").

## 7. Implemented API (all via gateway `http://localhost:8181`)

All responses use `{ "message": "..." }` except the offer.

| Endpoint | Service | Success | Errors |
|---|---|---|---|
| `POST /sim/validation` | sim-service | 200 `Valid details. Welcome!!.` | 400 format, 404 invalid details, 409 `SIM already active` |
| `GET /sim/offer/{simNumber}` | sim-service | 200 OfferDTO | 404 |
| `POST /sim/activation` `{simNumber}` | sim-service | 200 `SIM activated successfully` | 404, 409 |
| `POST /customer/validation` `{email, dob}` | customer-service | 200 | 400 format / `Invalid details`, 404 `No request placed for you.` |
| `POST /customer/details` `{email, firstName, lastName}` | customer-service | 200 | 400 `Invalid details`, 404 |
| `POST /customer/activate` | customer-service → Feign → sim-service | 200 `SIM activated successfully` | 400 validation, 404, 409 passed through from sim-service, 503 if sim-service is unreachable |
| `POST /idproof/validation` | idproof-service | 200 `ID proof validated successfully` | 400 `Invalid details` |

Gateway routes live in `config-server/.../config/api-gateway.properties` (`lb://` + Eureka). The gateway adds CORS headers for `http://localhost:5500` (VS Code Live Server) and `:8080`.

## 8. UI fixes applied

- Address-page state `<select>` had a duplicate `id="title1"`; renamed to `state1`.
- `aler(response)` typo and an undefined `dob1` reference stopped the ID-proof page from advancing; removed.
- The address state is now saved to `localStorage` (it was previously set only by the Google geocoding call, which fails without an API key).
- The offer is fetched only after SIM validation succeeds.
- The personal-details page calls `/customer/details`.
- A safe `errorMessage()` helper replaces direct `error.responseJSON.message` access.

## 9. Security (Phase 7, implemented)

- `POST /auth/login {username, password}` on the gateway returns `{token, tokenType: "Bearer", expiresIn}`. The token is an HS256 JWT valid for 30 minutes, with claims `iss`, `sub` and `scope`.
- Users are configured in `config-server/.../api-gateway.properties` as `app.auth.users.<name>=<bcrypt>`. Demo login: **customer / Customer@123**.
- The gateway rejects routed requests without a valid token (401 `{"message": ...}`). `/auth/login` and `/actuator/health` stay public.
- **Defence in depth:** each service also verifies the JWT (same secret from the config server), so calling ports 5000–5002 directly without a token returns 401.
- **Token relay:** customer-service's Feign client copies the incoming `Authorization` header to sim-service (`FeignTokenRelayConfig`).
- **UI:** a login modal appears on load and on any 401. The token is kept in `sessionStorage` and sent only to the gateway URL.
- **Dev discovery tuning:** registry fetch and load-balancer cache are set to 5s, so the gateway routes within seconds of startup.

## 10. Remaining phases

- Phase 8: Resilience4j fallbacks (Feign `SimClient` fallback + gateway circuit-breaker route filter).
- Phase 9: Zipkin tracing (the dependency is present; a server must be started on 9411), file logging (already configured centrally), actuator monitoring.
- Phase 10: Full UI walkthrough.
