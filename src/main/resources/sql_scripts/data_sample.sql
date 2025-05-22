-- Insert data for StateMachineDefinition table

-- Definition 1: Standard Four-State Workflow
INSERT INTO STATE_MACHINE_DEFINITION (ID, name) VALUES (10, 'PreWorkability');

-- Definition 2: Alternative Four-State Workflow
INSERT INTO STATE_MACHINE_DEFINITION (ID, name) VALUES (20, 'Booking');


-- Insert data for STATE table (for Definition 1: Standard Workflow)

-- States for Standard Workflow (machine_id = 10)
-- ID 11: Initiated (Initial State)
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (11, 'Initiated', TRUE, FALSE, 10, 'CREATOR');

-- ID 12: Processing
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (12, 'Processing', FALSE, FALSE, 10, 'PROCESSOR');

-- ID 13: Validated
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (13, 'Validated', FALSE, FALSE, 10, 'VALIDATOR');

-- ID 14: Completed (End State)
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (14, 'Completed', FALSE, TRUE, 10, 'NONE');


-- Insert data for TRANSITION table (for Definition 1: Standard Workflow)

-- Transitions for Standard Workflow (machine_id = 10)
-- ID 101: Initiated -> Processing on event START
INSERT INTO TRANSITION (ID, source_id, target_id, event, machine_id)
VALUES (101, 11, 12, 'START', 10);

-- ID 102: Processing -> Validated on event VALIDATE
INSERT INTO TRANSITION (ID, source_id, target_id, event, machine_id)
VALUES (102, 12, 13, 'VALIDATE', 10);

-- ID 103: Validated -> Completed on event COMPLETE
INSERT INTO TRANSITION (ID, source_id, target_id, event, machine_id)
VALUES (103, 13, 14, 'COMPLETE', 10);


-- Insert data for STATE table (for Definition 2: Alternative Workflow)

-- States for Alternative Workflow (machine_id = 20)
-- ID 21: Initiated (Initial State)
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (21, 'Initiated', TRUE, FALSE, 20, 'USER');

-- ID 22: Processing
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (22, 'Processing', FALSE, FALSE, 20, 'AGENT');

-- ID 23: Validated
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (23, 'Validated', FALSE, FALSE, 20, 'MANAGER');

-- ID 24: Completed (End State)
INSERT INTO STATE (ID, name, is_initial, is_end_state, machine_id, default_roles)
VALUES (24, 'Completed', FALSE, TRUE, 20, 'CLOSED');


-- Insert data for TRANSITION table (for Definition 2: Alternative Workflow)

-- Transitions for Alternative Workflow (machine_id = 20)
-- ID 201: Initiated -> Processing on event BEGIN
INSERT INTO TRANSITION (ID, source_id, target_id, event, machine_id)
VALUES (201, 21, 22, 'START', 20);

-- ID 202: Processing -> Validated on event REVIEW
INSERT INTO TRANSITION (ID, source_id, target_id, event, machine_id)
VALUES (202, 22, 23, 'VALIDATE', 20);

-- ID 203: Validated -> Completed on event FINISH
INSERT INTO TRANSITION (ID, source_id, target_id, event, machine_id)
VALUES (203, 23, 24, 'COMPLETE', 20);

