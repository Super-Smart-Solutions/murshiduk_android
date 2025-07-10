package com.saatco.murshadik;

import java.util.Collection;

public interface IChatUserOnline {
     void onOnlineList(Collection<Integer> onlineIds);
     void onJoinUser(Integer chatId);
     void onLeaveUser(Integer chatId);
}
