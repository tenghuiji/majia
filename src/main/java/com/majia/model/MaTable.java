package com.majia.model;

import com.majia.enumerate.Action;

import java.util.*;
import java.util.stream.Collectors;

public class MaTable {
    protected String tableId;
    protected MaJiang maJiang;
    public List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
    protected Player dealerPlayer;//庄家
    protected int turn=0;//当前说话玩家
    protected   int count=2;//可以玩的局数，1局4把
    protected  boolean actionConfirm=false;

    //桌面上麻将
    protected Queue<MaItem> leftMaItems = new LinkedList<MaItem>();

    public MaTable(int count) {
        this.count=count;
        maJiang = new MaJiang();
        this.tableId = UUID.randomUUID().toString();
    }

    public MaTable(boolean _wind,int count) {
        this.count=count;
        maJiang = new MaJiang(_wind);
        this.tableId = UUID.randomUUID().toString();
    }

    public Player getDealerPlayer() {
        return dealerPlayer;
    }

    public void setDealerPlayer(Player dealerPlayer) {
        this.dealerPlayer = dealerPlayer;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    //洗牌，并且码牌
    public void initMaItems() {
        for(Player player:players)
            player.clear();
        Set<Integer> flush = new LinkedHashSet<Integer>();
        //一副崭新并且排列好的麻将
        List<MaItem> maItems = maJiang.getMaItems();
        //随机
        Random random = new Random();
        int size = maItems.size();
        while (flush.size() != size) {
            flush.add(random.nextInt(size));
        }
        //码牌
        for (Integer i : flush) {
            leftMaItems.add(maItems.get(i));
        }
    }

    //玩家拿牌
    public void initPlayerMaItems() {
        int s = players.size();//玩家人数
        //先拿12张牌
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < s; j++) {
                players.get(j).initHandMaItem_4(leftMaItems);//拿四张
            }
        }
        for (int j = 0; j < s; j++) {
            players.get(j).initHandMaItem_1(leftMaItems);//拿1张
        }
        //庄家再拿一张
        //players.get(0).initHandMaItem_1(leftMaItems);
    }

    public int getPlayerCount(){
        return players.size();
    }
    /**
     * 根据用户名找到玩家
     * @param pid
     * @return
     */
    public Player getPlayer(String pid){
        for(Player player:players){
            if (player.getPid().equals(pid)){
                return player;
            }
        }
        return  null;
    }

    protected void setPlayerTurn(Player player) {
        for(int i=0;i<players.size();i++){
            if(players.get(i)==player){
                turn=i;
                break;
            }
        }
    }
    /**
     * 玩家拿一张牌
     * @param pid
     */
    public List<Action> getIn(String pid){
        for(Player player:players) {
            if (player.getPid().equals(pid)) {
                return player.getIn(leftMaItems);
            }
        }
        return null;
    }
    /**
     * 初始化玩家
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     */
    public void initPlay(Player p1, Player p2, Player p3, Player p4) {
        players.clear();
        dealerPlayer = p1;//庄家
        p1.setPlayingOut(true);
        turn = 0;
        if (p1 != null) players.add(p1);
        if (p2 != null) {
            p2.setPlayingOut(false);
            players.add(p2);
        }
        if (p3 != null) {
            p3.setPlayingOut(false);
            players.add(p3);
        }
        if (p4 != null) {
            p4.setPlayingOut(false);
            players.add(p4);
        }
    }

    //下一个玩家说话
    private void turn() {
        turn = (turn + 1) % players.size();
    }

    //碰牌说话位置发生变化
    public void setTurn(int _turn) {
        this.turn = _turn;
    }

    //摇骰子
    public String dice() {
        return this.maJiang.dice();
    }

    public Queue<MaItem> getLeftMaItems() {
        return this.leftMaItems;
    }

    /**
     * 玩家是否行为都确定了
     * @return
     */
    public boolean isPlayerConfirm()
    {
        for(Player player:players)
            if(!player.isActionConfirm())
                return false;
        return true;
    }
    /**
     * 玩家是否行为都确定了
     * @return
     */
    public void setPlayernextConfirm()
    {
        for(Player player:players)
            player.setActionConfirm(false);
    }
    /**
     * 玩家打牌
     * @param player
     * @param item
     * @param action
     */
    public List<Action> play(Player player, List<MaItem> handItem,MaItem item, Action action) {
        if(players.get(turn)==player){//该自己说话
            turn();
            if(action==Action.ANGANG){//暗杠
                player.doGang(item);
                return player.getInAtlast(leftMaItems);
            }else if(action==Action.HUITOUGANG) {
                player.doHuiTouGang(item);
                return player.getInAtlast(leftMaItems);
            }else if(action==Action.HU) {
                //TODO 算钱
                System.out.println("一局结束算钱！");
            }else if(action==Action.PLAYING){
                player.playOut(item);
            }
        }else {//其他玩家打牌，自己说话，胡杠
            setPlayerTurn(player);
            if(action==Action.GANG){//暗杠
                player.doGang(item);
                return player.getInAtlast(leftMaItems);
            }else if(action==Action.PENG ) {
                player.doPeng(item);
            }else if(action==Action.HU) {
                //TODO 算钱
            }else if(action == Action.EAT){//吃牌
                player.doChi(handItem,item);
            }
            else {
                player.setActionConfirm(true);
            }
        }
        return  null;
    }

    /**
     * 可以对别人打的牌做出的行为
     * @param player
     * @param item
     * @return
     */
    public Map<List<Action>,List<List<MaItem>>> getPlayerAction(Player player,MaItem item) {
        Map<List<Action>,List<List<MaItem>>> resMap = new HashMap<>();

        List<Action>actions = new ArrayList<>();
        List<List<MaItem>> maItemList = new ArrayList<>();
        resMap.put(actions,maItemList);

        if(player.canGang(item)) {
            actions.add(Action.GANG);
        }
        if(player.canPeng(item)) {
            actions.add(Action.PENG);
        }
        if(player.canHu(item)!=null) {
            actions.add(Action.HU);
        }
        //判断响应吃牌
        if(!player.isFeng(item)){ //风不能吃
            List<MaItem> paiList;
            //是剑的情况
            if(player.isJian(item)){
                paiList = player.getHandMaItems()
                        .stream()
                        .filter(itemFilter->player.isJian(itemFilter))
                        .collect(Collectors.toList());
            }else {
                //获取所有不是风和剑,并且花色相同的牌
                paiList = player
                        .getHandMaItems()
                        .stream()
                        .filter(itemFilter -> !player.isFeng(itemFilter))
                        .filter(itemFilterType -> itemFilterType.getType() == item.getType())
                        .collect(Collectors.toList());
            }
            int paiListSize = paiList.size();
            if(paiListSize >= 2){ //至少手中有两张相同的余牌
                for(int i=0; i < paiListSize; ++i){
                    List<MaItem> tmpList = new ArrayList<>();
                    tmpList.add(paiList.get(i));
                    for(int j=i+1; j < paiListSize; ++j){
                        tmpList.add(paiList.get(j));
                        if(player.canChi(tmpList,item)){
                            maItemList.add(Arrays.asList(tmpList.get(0),tmpList.get(1)));
                            tmpList.remove(paiList.get(j));
                            continue;
                        }else{
                            tmpList.remove(paiList.get(j));
                        }
                    }
                }
            }

            if(maItemList.size() > 1){
                actions.add(Action.EAT);
            }
        }

        actions.add(Action.PASS);
        return resMap;
    }
}
