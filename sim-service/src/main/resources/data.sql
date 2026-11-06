INSERT INTO offer (offer_id, offer_name, call_qty, data_qty, duration, cost)
VALUES (5000, 'Welcome Offer', 100, 120, 10, 20);

INSERT INTO offer (offer_id, offer_name, call_qty, data_qty, duration, cost)
VALUES (5001, 'Premium Offer', 500, 1000, 30, 99);

INSERT INTO offer (offer_id, offer_name, call_qty, data_qty, duration, cost)
VALUES (5002, 'Value Offer', 200, 500, 15, 49);

INSERT INTO sim (sim_number, service_number, status, offer_id)
VALUES ('1234567891234', '1234567891', 'INACTIVE', 5000);

INSERT INTO sim (sim_number, service_number, status, offer_id)
VALUES ('1234567891235', '1234567892', 'ACTIVE', 5001);
