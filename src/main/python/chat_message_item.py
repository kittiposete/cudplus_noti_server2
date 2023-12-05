class ChatMessageItem:
    def __init__(self, sender_name, chat_room, message_content):
        self.sender_name = sender_name
        self.chat_room = chat_room
        self.message_content = message_content

    def __str__(self):
        return "ChatMessageItem(sender_name={}, chat_room={}, message_content={})".format(
            self.sender_name, self.chat_room, self.message_content)

    def toString(self):
        return "ChatMessageItem(sender_name={}, chat_room={}, message_content={})".format(
            self.sender_name, self.chat_room, self.message_content)
