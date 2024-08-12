package com.scm.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    
    // what is inside the message
    private String content;

    // what is the color of message
    //@Builder.Default -> to set by default value
    @Builder.Default
    private MessageType type = MessageType.blue;
}
