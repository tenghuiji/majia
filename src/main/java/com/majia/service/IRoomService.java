package com.majia.service;

import com.majia.enumerate.Direction;
import com.majia.model.MaTable;

public interface IRoomService {
    public MaTable getRoom(String tableId);
    public MaTable createRoom(String userId,int count);
    public Direction joinRoom(String userId, String tableId);
    public boolean start(String userId, String tableId);
}
