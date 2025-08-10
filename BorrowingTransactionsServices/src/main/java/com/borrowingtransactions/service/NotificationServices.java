package com.borrowingtransactions.service;

import java.util.List;

import com.borrowingtransactions.DTO.NotificationDTO;


public interface NotificationServices {
    List<NotificationDTO> getAllNotifications();
    List<NotificationDTO> getNotificationsByMemberId(Long memberId);
    

    public List<NotificationDTO> generateNotifications();
}
