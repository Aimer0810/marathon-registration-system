CREATE TABLE IF NOT EXISTS ai_chat_message (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NULL,
    question TEXT NOT NULL,
    answer LONGTEXT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ai_chat_user_id (user_id),
    INDEX idx_ai_chat_event_id (event_id)
);
