-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-05-13 04:24:54.018

-- tables
-- Table: account_recovery
CREATE TABLE account_recovery (
    account_recovery_id serial  NOT NULL,
    user_id int  NOT NULL,
    hash_code varchar(255)  NOT NULL,
    expiration_date timestamp  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(255)  NOT NULL,
    tx_host varchar(255)  NOT NULL,
    CONSTRAINT account_recovery_pk PRIMARY KEY (account_recovery_id)
);

-- Table: file
CREATE TABLE file (
    file_id serial  NOT NULL,
    content_type varchar(255)  NOT NULL,
    filename varchar(255)  NOT NULL,
    file_data bytea  NOT NULL,
    is_picture boolean  NOT NULL,
    thumbnail bytea  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT file_pk PRIMARY KEY (file_id)
);

-- Table: group
CREATE TABLE "group" (
    group_id serial  NOT NULL,
    group_name varchar(100)  NOT NULL,
    group_description varchar(255)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT group_pk PRIMARY KEY (group_id)
);

-- Table: group_role
CREATE TABLE group_role (
    group_role_id serial  NOT NULL,
    group_id int  NOT NULL,
    role_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT group_role_pk PRIMARY KEY (group_role_id)
);

-- Table: h_loaned_tool
CREATE TABLE h_loaned_tool (
    h_loaned_tool_id int  NOT NULL,
    task_id int  NOT NULL,
    tool_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT h_loaned_tool_pk PRIMARY KEY (h_loaned_tool_id)
);

-- Table: h_task
CREATE TABLE h_task (
    h_task_id serial  NOT NULL,
    project_id int  NOT NULL,
    task_name varchar(100)  NOT NULL,
    task_description varchar(255)  NOT NULL,
    task_deadline timestamp  NOT NULL,
    task_priority int  NOT NULL,
    task_statu_id int NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT h_task_pk PRIMARY KEY (h_task_id)
);

-- Table: h_task_assignee
CREATE TABLE h_task_assignee (
    h_task_assignee_id serial  NOT NULL,
    task_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT h_task_assignee_pk PRIMARY KEY (h_task_assignee_id)
);

-- Table: h_task_comment
CREATE TABLE h_task_comment (
    h_task_comment_id serial  NOT NULL,
    task_id int  NOT NULL,
    user_id int  NOT NULL,
    comment_number int  NOT NULL,
    comment varchar(255)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT h_task_comment_pk PRIMARY KEY (h_task_comment_id)
);

-- Table: h_task_comment_file
CREATE TABLE h_task_comment_file (
    h_task_comment_file_id serial  NOT NULL,
    task_comment_id int  NOT NULL,
    file_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT h_task_comment_file_pk PRIMARY KEY (h_task_comment_file_id)
);

-- Table: h_tool
CREATE TABLE h_tool (
    h_tool_id serial  NOT NULL,
    file_photo_id int  NOT NULL,
    tool_code varchar(50)  NOT NULL,
    tool_name varchar(50)  NOT NULL,
    tool_description varchar(255)  NOT NULL,
    available boolean  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT h_tool_pk PRIMARY KEY (h_tool_id)
);

-- Table: loaned_tool
CREATE TABLE loaned_tool (
    loaned_tool_id int  NOT NULL,
    task_id int  NOT NULL,
    tool_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT loaned_tool_pk PRIMARY KEY (loaned_tool_id)
);

-- Table: notification
CREATE TABLE notification (
    notification_id serial  NOT NULL,
    user_id int  NOT NULL,
    message varchar(255)  NOT NULL,
    sent_date timestamp  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT notification_pk PRIMARY KEY (notification_id)
);

-- Table: project
CREATE TABLE project (
    project_id serial  NOT NULL,
    project_name varchar(100)  NOT NULL,
    project_description text  NOT NULL,
    date_from timestamp  NOT NULL,
    date_to timestamp  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT project_pk PRIMARY KEY (project_id)
);

-- Table: project_member
CREATE TABLE project_member (
    project_member_id serial  NOT NULL,
    project_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT project_member_pk PRIMARY KEY (project_member_id)
);

-- Table: project_moderator
CREATE TABLE project_moderator (
    project_moderator_id serial  NOT NULL,
    project_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT project_moderator_pk PRIMARY KEY (project_moderator_id)
);

-- Table: project_owner
CREATE TABLE project_owner (
    project_owner_id serial  NOT NULL,
    project_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT project_owner_pk PRIMARY KEY (project_owner_id)
);

-- Table: role
CREATE TABLE role (
    role_id serial  NOT NULL,
    role_name varchar(100)  NOT NULL,
    role_description varchar(255)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT role_pk PRIMARY KEY (role_id)
);

-- Table: task
CREATE TABLE task (
    task_id serial  NOT NULL,
    project_id int  NOT NULL,
    task_status_id int  NOT NULL,
    task_name varchar(100)  NOT NULL,
    task_description varchar(255)  NOT NULL,
    task_deadline timestamp  NOT NULL,
    task_priority int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_pk PRIMARY KEY (task_id)
);

-- Table: task_assignee
CREATE TABLE task_assignee (
    task_assignee_id serial  NOT NULL,
    task_id int  NOT NULL,
    user_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_assignee_pk PRIMARY KEY (task_assignee_id)
);

-- Table: task_comment
CREATE TABLE task_comment (
    task_comment_id serial  NOT NULL,
    task_id int  NOT NULL,
    user_id int  NOT NULL,
    comment_number int  NOT NULL,
    comment varchar(255)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_comment_pk PRIMARY KEY (task_comment_id)
);

-- Table: task_comment_file
CREATE TABLE task_comment_file (
    task_comment_file_id serial  NOT NULL,
    task_comment_id int  NOT NULL,
    file_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_comment_file_pk PRIMARY KEY (task_comment_file_id)
);

-- Table: task_file
CREATE TABLE task_file (
    task_file_id serial  NOT NULL,
    task_id int  NOT NULL,
    file_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_file_pk PRIMARY KEY (task_file_id)
);

-- Table: task_review
CREATE TABLE task_review (
    task_review_id serial  NOT NULL,
    task_id int  NOT NULL,
    rating int  NOT NULL,
    comment varchar(255)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_review_pk PRIMARY KEY (task_review_id)
);

-- Table: task_status
CREATE TABLE task_status (
    task_status_id serial  NOT NULL,
    task_status_name varchar(50)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT task_status_pk PRIMARY KEY (task_status_id)
);

-- Table: tool
CREATE TABLE tool (
    tool_id serial  NOT NULL,
    file_photo_id int  NOT NULL,
    tool_code varchar(50)  NOT NULL,
    tool_name varchar(50)  NOT NULL,
    tool_description varchar(255)  NOT NULL,
    available boolean  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT tool_pk PRIMARY KEY (tool_id)
);

-- Table: user
CREATE TABLE "user" (
    user_id serial  NOT NULL,
    file_photo_id int  NOT NULL,
    first_name varchar(100)  NOT NULL,
    last_name varchar(100)  NOT NULL,
    email varchar(100)  NOT NULL,
    username varchar(100)  NOT NULL,
    password varchar(100)  NOT NULL,
    phone varchar(50)  NOT NULL,
    description varchar(255)  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT user_id PRIMARY KEY (user_id)
);

-- Table: user_group
CREATE TABLE user_group (
    user_group_id serial  NOT NULL,
    user_id int  NOT NULL,
    group_id int  NOT NULL,
    status boolean  NOT NULL,
    tx_date timestamp  NOT NULL,
    tx_user varchar(100)  NOT NULL,
    tx_host varchar(100)  NOT NULL,
    CONSTRAINT user_group_pk PRIMARY KEY (user_group_id)
);

-- foreign keys
-- Reference: account_recovery_user (table: account_recovery)
ALTER TABLE account_recovery ADD CONSTRAINT account_recovery_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: group_role_group (table: group_role)
ALTER TABLE group_role ADD CONSTRAINT group_role_group
    FOREIGN KEY (group_id)
    REFERENCES "group" (group_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: group_role_role (table: group_role)
ALTER TABLE group_role ADD CONSTRAINT group_role_role
    FOREIGN KEY (role_id)
    REFERENCES role (role_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: loaned_tool_task (table: loaned_tool)
ALTER TABLE loaned_tool ADD CONSTRAINT loaned_tool_task
    FOREIGN KEY (task_id)
    REFERENCES task (task_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: loaned_tool_tool (table: loaned_tool)
ALTER TABLE loaned_tool ADD CONSTRAINT loaned_tool_tool
    FOREIGN KEY (tool_id)
    REFERENCES tool (tool_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: loaned_tool_user (table: loaned_tool)
ALTER TABLE loaned_tool ADD CONSTRAINT loaned_tool_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: notification_user (table: notification)
ALTER TABLE notification ADD CONSTRAINT notification_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: project_moderator_project (table: project_moderator)
ALTER TABLE project_moderator ADD CONSTRAINT project_moderator_project
    FOREIGN KEY (project_id)
    REFERENCES project (project_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: project_moderator_user (table: project_moderator)
ALTER TABLE project_moderator ADD CONSTRAINT project_moderator_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: project_owner_project (table: project_owner)
ALTER TABLE project_owner ADD CONSTRAINT project_owner_project
    FOREIGN KEY (project_id)
    REFERENCES project (project_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: project_owner_user (table: project_owner)
ALTER TABLE project_owner ADD CONSTRAINT project_owner_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: project_team_project (table: project_member)
ALTER TABLE project_member ADD CONSTRAINT project_team_project
    FOREIGN KEY (project_id)
    REFERENCES project (project_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: project_team_user (table: project_member)
ALTER TABLE project_member ADD CONSTRAINT project_team_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_assignee_task (table: task_assignee)
ALTER TABLE task_assignee ADD CONSTRAINT task_assignee_task
    FOREIGN KEY (task_id)
    REFERENCES task (task_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_assignee_user (table: task_assignee)
ALTER TABLE task_assignee ADD CONSTRAINT task_assignee_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_comment_file_file (table: task_comment_file)
ALTER TABLE task_comment_file ADD CONSTRAINT task_comment_file_file
    FOREIGN KEY (file_id)
    REFERENCES file (file_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_comment_file_task_comment (table: task_comment_file)
ALTER TABLE task_comment_file ADD CONSTRAINT task_comment_file_task_comment
    FOREIGN KEY (task_comment_id)
    REFERENCES task_comment (task_comment_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_comment_task (table: task_comment)
ALTER TABLE task_comment ADD CONSTRAINT task_comment_task
    FOREIGN KEY (task_id)
    REFERENCES task (task_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_comment_user (table: task_comment)
ALTER TABLE task_comment ADD CONSTRAINT task_comment_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_file_file (table: task_file)
ALTER TABLE task_file ADD CONSTRAINT task_file_file
    FOREIGN KEY (file_id)
    REFERENCES file (file_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_file_task (table: task_file)
ALTER TABLE task_file ADD CONSTRAINT task_file_task
    FOREIGN KEY (task_id)
    REFERENCES task (task_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_project (table: task)
ALTER TABLE task ADD CONSTRAINT task_project
    FOREIGN KEY (project_id)
    REFERENCES project (project_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_review_task (table: task_review)
ALTER TABLE task_review ADD CONSTRAINT task_review_task
    FOREIGN KEY (task_id)
    REFERENCES task (task_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: task_task_status (table: task)
ALTER TABLE task ADD CONSTRAINT task_task_status
    FOREIGN KEY (task_status_id)
    REFERENCES task_status (task_status_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: tool_file (table: tool)
ALTER TABLE tool ADD CONSTRAINT tool_file
    FOREIGN KEY (file_photo_id)
    REFERENCES file (file_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: user_file (table: user)
ALTER TABLE "user" ADD CONSTRAINT user_file
    FOREIGN KEY (file_photo_id)
    REFERENCES file (file_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: user_group_group (table: user_group)
ALTER TABLE user_group ADD CONSTRAINT user_group_group
    FOREIGN KEY (group_id)
    REFERENCES "group" (group_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: user_group_user (table: user_group)
ALTER TABLE user_group ADD CONSTRAINT user_group_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (user_id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- End of file.
