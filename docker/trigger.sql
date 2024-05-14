--TRIGGERS FOR HISTORICAL TABLES
--H_LOANED_TOOL
CREATE OR REPLACE FUNCTION h_loaned_tool_ins()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_loaned_tool (h_loaned_tool_id, task_id, tool_id, user_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.loaned_tool_id, NEW.task_id, NEW.tool_id, NEW.user_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_loaned_tool_ins
AFTER INSERT ON loaned_tool
FOR EACH ROW EXECUTE PROCEDURE h_loaned_tool_ins();

CREATE OR REPLACE FUNCTION h_loaned_tool_upd()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_loaned_tool (h_loaned_tool_id, task_id, tool_id, user_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.loaned_tool_id, NEW.task_id, NEW.tool_id, NEW.user_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_loaned_tool_upd
AFTER UPDATE ON loaned_tool
FOR EACH ROW EXECUTE PROCEDURE h_loaned_tool_upd();

--H_TASK
CREATE OR REPLACE FUNCTION h_task_ins()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task (h_task_id, project_id, task_name, task_description, task_deadline, task_priority, task_status_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_id, NEW.project_id, NEW.task_name, NEW.task_description, NEW.task_deadline, NEW.task_priority, NEW.task_status_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_ins
AFTER INSERT ON task
FOR EACH ROW EXECUTE PROCEDURE h_task_ins();

CREATE OR REPLACE FUNCTION h_task_upd()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task (h_task_id, project_id, task_name, task_description, task_deadline, task_priority, task_status_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_id, NEW.project_id, NEW.task_name, NEW.task_description, NEW.task_deadline, NEW.task_priority, NEW.task_status_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_upd
AFTER UPDATE ON task
FOR EACH ROW EXECUTE PROCEDURE h_task_upd();

--H_TASK_ASSIGNEE
CREATE OR REPLACE FUNCTION h_task_assignee_ins()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task_assignee (h_task_assignee_id, task_id, user_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_assignee_id, NEW.task_id, NEW.user_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_assignee_ins
AFTER INSERT ON task_assignee
FOR EACH ROW EXECUTE PROCEDURE h_task_assignee_ins();

CREATE OR REPLACE FUNCTION h_task_assignee_upd()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task_assignee (h_task_assignee_id, task_id, user_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_assignee_id, NEW.task_id, NEW.user_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_assignee_upd
AFTER UPDATE ON task_assignee
FOR EACH ROW EXECUTE PROCEDURE h_task_assignee_upd();

--H_TASK_COMMENT
CREATE OR REPLACE FUNCTION h_task_comment_ins()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task_comment (h_task_comment_id, task_id, user_id, comment_number, comment, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_comment_id, NEW.task_id, NEW.user_id, NEW.comment_number, NEW.comment, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_comment_ins
AFTER INSERT ON task_comment
FOR EACH ROW EXECUTE PROCEDURE h_task_comment_ins();

CREATE OR REPLACE FUNCTION h_task_comment_upd()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task_comment (h_task_comment_id, task_id, user_id, comment_number, comment, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_comment_id, NEW.task_id, NEW.user_id, NEW.comment_number, NEW.comment, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_comment_upd
AFTER UPDATE ON task_comment
FOR EACH ROW EXECUTE PROCEDURE h_task_comment_upd();

--H_TASK_COMMENT_FILE
CREATE OR REPLACE FUNCTION h_task_comment_file_ins()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task_comment_file (h_task_comment_file_id, task_comment_id, file_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_comment_file_id, NEW.task_comment_id, NEW.file_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_comment_file_ins
AFTER INSERT ON task_comment_file
FOR EACH ROW EXECUTE PROCEDURE h_task_comment_file_ins();

CREATE OR REPLACE FUNCTION h_task_comment_file_upd()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_task_comment_file (h_task_comment_file_id, task_comment_id, file_id, status, tx_date, tx_user, tx_host)
    VALUES (NEW.task_comment_file_id, NEW.task_comment_id, NEW.file_id, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_task_comment_file_upd
AFTER UPDATE ON task_comment_file
FOR EACH ROW EXECUTE PROCEDURE h_task_comment_file_upd();

--H_TOOL
CREATE OR REPLACE FUNCTION h_tool_ins()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_tool (h_tool_id, file_photo_id, tool_name, tool_description, tool_code, available, status, tx_date, tx_user, tx_host)
    VALUES (NEW.tool_id, NEW.file_photo_id, NEW.tool_name, NEW.tool_description, NEW.tool_code, NEW.available, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_tool_ins
AFTER INSERT ON tool
FOR EACH ROW EXECUTE PROCEDURE h_tool_ins();

CREATE OR REPLACE FUNCTION h_tool_upd()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO h_tool (h_tool_id, file_photo_id, tool_name, tool_description, tool_code, available, status, tx_date, tx_user, tx_host)
    VALUES (NEW.tool_id, NEW.file_photo_id, NEW.tool_name, NEW.tool_description, NEW.tool_code, NEW.available, NEW.status, NEW.tx_date, NEW.tx_user, NEW.tx_host);
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER h_tool_upd
AFTER UPDATE ON tool
FOR EACH ROW EXECUTE PROCEDURE h_tool_upd();

