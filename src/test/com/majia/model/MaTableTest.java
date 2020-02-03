package com.majia.model;

import com.majia.enumerate.Action;
import com.majia.enumerate.Direction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class MaTableTest {
    private MaTable maTable;
    @Before
    public void setUp() throws Exception {
        maTable = new MaTable(true,4);
    }

    @Test
    public void getDealerPlayer() {
    }

    @Test
    public void setDealerPlayer() {
    }

    @Test
    public void getTableId() {
    }

    @Test
    public void setTableId() {
    }

    @Test
    public void getCount() {
    }

    @Test
    public void setCount() {
    }

    @Test
    public void initMaItems() {
    }

    @Test
    public void initPlayerMaItems() {
    }

    @Test
    public void getPlayer() {
    }

    @Test
    public void setPlayerTurn() {
    }

    @Test
    public void getIn() {
    }

    @Test
    public void initPlay() {
    }

    @Test
    public void turn() {
    }

    @Test
    public void setTurn() {
    }

    @Test
    public void dice() {
    }

    @Test
    public void getLeftMaItems() {
    }

    @Test
    public void isPlayerConfirm() {
    }

    @Test
    public void setPlayernextConfirm() {
    }

    @Test
    public void play() {
        Player player1 = new Player("1001");
        player1.setDealer(true);
        player1.setDirection(Direction.EAST);

        Player player2 = new Player("1002");
        player2.setDirection(Direction.EAST);

        Player player3 = new Player("1003");
        player3.setDirection(Direction.EAST);

        Player player4 = new Player("1004");
        player4.setDirection(Direction.EAST);

        maTable.initMaItems();
        maTable.initPlay(player1,player2,player3,player4);
        maTable.initPlayerMaItems();

        assertEquals("1001",player1.getPid());
        assertEquals(true,player1.isDealer());
        assertEquals(13,player1.getHandMaItems().size());
        assertEquals(Direction.EAST,player1.getDirection());

        assertEquals("1002",player2.getPid());
        assertEquals(false,player2.isDealer());
        assertEquals(13,player2.getHandMaItems().size());
        assertEquals(Direction.EAST,player2.getDirection());

        assertEquals("1003",player3.getPid());
        assertEquals(false,player3.isDealer());
        assertEquals(13,player3.getHandMaItems().size());
        assertEquals(Direction.EAST,player3.getDirection());

        assertEquals("1004",player4.getPid());
        assertEquals(false,player4.isDealer());
        assertEquals(13,player4.getHandMaItems().size());
        assertEquals(Direction.EAST,player4.getDirection());

        //桌上玩家数量
        assertEquals(4,maTable.getPlayerCount());
        //桌上剩余牌数量
        assertEquals(84,maTable.getLeftMaItems().size());

        //桌上玩家总分数
        assertEquals(4000,player1.getScore()+player2.getScore()+player3.getScore()+player4.getScore());

        System.out.println(player1);
        System.out.println(player2);
        System.out.println(player3);
        System.out.println(player4);
        System.out.println("剩余麻将:"+maTable.getLeftMaItems().size()+"张");
        System.out.println(maTable.getLeftMaItems());
        System.out.println(UUID.randomUUID().toString());

        MaItem currentMaItem = new MaItem(null);
        //庄稼出牌
        for(int i = 0; i < 100; ++i) {
            Player turnPlayer = maTable.players.get(i);
            //可以先自杠，胡牌
            //maTable.play(turnPlayer, null, turnPlayer.getHandMaItems().get(1), Action.PLAYING);

            for (Player player: maTable.players) {
                //下家出牌
                List<Action> actions = player.getIn(maTable.getLeftMaItems());
                //刚拿到手的一张牌
                currentMaItem = turnPlayer.getNewMaItem();

                for (Action action : actions){
                    if(action == Action.PLAYING){
                        MaItem daMaItem = player.doDa();
                    }
                    maTable.play(player,null,currentMaItem,action);
                }

                Map<List<Action>, List<List<MaItem>>> actionMap = maTable.getPlayerAction(player, currentMaItem);
                MaItem finalCurrentMaItem = currentMaItem;
                actionMap
                        .entrySet()
                        .stream()
                        .forEach(item -> {
                            item.getKey()
                                    .stream()
                                    .forEach(action -> {
                                        if (action == Action.EAT) {
                                            maTable.play(player, item.getValue().get(0), finalCurrentMaItem, action);
                                        } else {
                                            maTable.play(player, null, finalCurrentMaItem, action);
                                        }
                                    });
                        });
            }
        }
            //maTable.getIn(player.getPid());

            //maTable.play(player, null,player.getHandMaItems().get(1), Action.PLAYING);

    }

    @Test
    public void getPlayerAction() {
    }
}
