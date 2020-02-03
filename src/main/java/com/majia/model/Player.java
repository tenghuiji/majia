package com.majia.model;

import com.majia.enumerate.*;
import com.majia.expection.XiangGongException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Player {
    private static final Logger logger = LoggerFactory.getLogger(Player.class);
    private boolean isWatcher = false;//观战选手
    private String pid;
    private Direction direction;//玩家的方位
    private int score=1000;
    public Map<String,Integer> scoreMap=new HashMap<String, Integer>();
    public boolean isDealer;//是否是庄家
    private int dealNum;//连庄次数
    private List<MaItem> initMaItems = new ArrayList<MaItem>();//起始的麻将
    private List<MaItem> handMaItems = new LinkedList<MaItem>();//手里面的麻将
    private List<MaItem> inMaItems = new ArrayList<MaItem>();//碰、杠、吃、蛋的麻将
    private Stack<MaItem> outMaItems = new Stack<MaItem>();//打出去的麻将
    private boolean isPlayingOut = false;//是否是等待出牌的玩家
    private MaItem newMaItem=null;//刚拿进的一张牌
    private  volatile boolean actionConfirm=false;

    public Player(String _pid) {
        this.pid = _pid;
    }
    public void clear() {
        initMaItems.clear();
        handMaItems.clear();
        inMaItems.clear();
        outMaItems.clear();
    }
    private List<MaItem> buildHand(MaItem item) {
        if (handMaItems.size() < 15)
            insertMaItem(item);
        return handMaItems;
    }

    /**
     * 麻将结束时，得到打出的一种麻将的数量
     * @param item
     * @return
     */
    public int getMaItemNum(MaItem item) {
        int num=0;
        for(MaItem temp:handMaItems) {
            if(temp.equals(item))
                num++;
        }
        for(MaItem temp:inMaItems) {
            if(temp.equals(item))
                num++;
        }
        for(MaItem temp:outMaItems) {
            if(temp.equals(item))
                num++;
        }
        return num;
    }
    /**
     * d当其他玩家打出牌的时候的行为
     * @param item
     * @return
     */
    public List<Action>getActionsWhenOut(MaItem item)
    {
        List<Action>actions=new ArrayList<Action>();
        if(canHu(item)!=null)
        {
            actions.add(Action.HU);
        }
        if(canGang(item))
        {
            actions.add(Action.GANG);
        }
        if(canPeng(item))
        {
            actions.add(Action.PENG);
        }
        return actions;
    }
    /**
     * 玩家拿到牌后可以的行为
     * 杠或者胡
     * @param item
     */
    private List<Action> getActionsWhenIn(MaItem item) {
        List<Action>actions=new ArrayList<Action>();
        if(canHu(item)!=null)
        {
            actions.add(Action.HU);
        }
        if(canGang(item))
        {
            actions.add(Action.GANG);
        }
        if(canHuiTouGang(item))
        {
            actions.add(Action.HUITOUGANG);
        }
        if(actions.isEmpty()){
            actions.add(Action.PLAYING);
        }
        return actions;
    }
    //四张一拿
    public void initHandMaItem_4(Queue<MaItem> leftMaItems) {
        for (int i = 0; i < 4; i++) {
            MaItem maItem = leftMaItems.poll();
            insertMaItem(maItem);
        }
    }

    //一张一拿
    public void initHandMaItem_1(Queue<MaItem> leftMaItems) {
        MaItem maItem = leftMaItems.poll();
        insertMaItem(maItem);
        newMaItem=maItem;
    }

    //拿一张
    public List<Action> getIn(Queue<MaItem> leftMaItems) {
        MaItem maItem = leftMaItems.poll();
        List<Action> actions = getActionsWhenIn(maItem);
        insertMaItem(maItem);
        newMaItem=maItem;
        return actions;
    }

    /**
     * 杠的时候从后面拿一张牌
     * @param leftMaItems
     * @return
     */
    public List<Action> getInAtlast(Queue<MaItem> leftMaItems) {
        MaItem maItem = ((LinkedList<MaItem>)leftMaItems).getLast();
        insertMaItem(maItem);
        newMaItem=maItem;
        return getActionsWhenOut(maItem);
    }

    public boolean isActionConfirm() {
        return actionConfirm;
    }

    public void setActionConfirm(boolean actionConfirm) {
        this.actionConfirm = actionConfirm;
    }

    /**
     * 玩家碰牌
     * @param item
     */
    public boolean doPeng(MaItem item)
    {
        if (canPeng(item))
        {
            int i=0;
            inMaItems.add(item);
            for(MaItem ma:handMaItems)
            {
                if(i==2)
                    break;
                if(ma.equals(item))
                {
                    inMaItems.add(ma);
                    handMaItems.remove(ma);
                    i++;
                }
            }
            return  true;
        }else {
            return  false;
        }
    }

    /**
     * 玩家杠牌
     * @param item
     */
    public Action doGang(MaItem item)
    {
        if (canGang(item))
        {
            int i=3;
            inMaItems.add(item);
            for(MaItem ma:handMaItems)
            {
                if(i==3)
                    break;
                if(ma.equals(item))
                {
                    inMaItems.add(ma);
                    handMaItems.remove(ma);
                    i++;
                }
            }
            return  Action.ANGANG;
        }
        else {
            return  null;
        }
    }
    /**
     * 玩家回头杠
     * @param item
     */
    public Action doHuiTouGang(MaItem item)
    {
        if (canHuiTouGang(item))
        {
            for(int i=0;i<inMaItems.size();i++)
            {
                if(item.equals(inMaItems.get(i)))
                {
                    inMaItems.add(i,item);
                }
            }
            return  Action.HUITOUGANG;
        }
        else {
            return  null;
        }
    }

    /**
     * 玩家吃牌
     */
    public Action doChi(List<MaItem> chiList,MaItem item){
        if(canChi(chiList,item)){
            inMaItems.add(item);

            inMaItems.add(chiList.get(0));
            handMaItems.remove(handMaItems.get(handMaItems.indexOf(chiList.get(0))));

            inMaItems.add(chiList.get(1));
            handMaItems.remove(handMaItems.get(handMaItems.indexOf(chiList.get(1))));

            inMaItems = sorted(initMaItems);

            return Action.EAT;
        }
        return null;
    }

    /**
     * 玩家打牌
     */
    public MaItem doDa(){
        List<List<MaItem>> maItem = getPair();
        return handMaItems.get(0);
    }

    /**
     * 得到听牌
     *
     * @return
     */
    public Map<MaItem, Integer> getWaitingMaItem() {
        Map<MaItem, Integer> waitingMap = new LinkedHashMap<MaItem, Integer>();
        for (int i = 0; i < 9; i++) {
            MaValue value = MaValue.getMaValue(i + 1);
            updateWaitingMap(waitingMap, MaType.WAN, value);
            updateWaitingMap(waitingMap, MaType.TIAO, value);
            updateWaitingMap(waitingMap, MaType.TONG, value);
        }
        updateWaitingMap(waitingMap, MaType.DONG, null);
        updateWaitingMap(waitingMap, MaType.NAN, null);
        updateWaitingMap(waitingMap, MaType.XI, null);
        updateWaitingMap(waitingMap, MaType.BEI, null);
        updateWaitingMap(waitingMap, MaType.ZHONG, null);
        updateWaitingMap(waitingMap, MaType.FA, null);
        updateWaitingMap(waitingMap, MaType.BAI, null);
        return waitingMap;
    }

    private void updateWaitingMap(Map<MaItem, Integer> waitingMap, MaType type, MaValue value) {
        MaItem item = new MaItem(type, value);
        HuType huType = canHu(item);
        if (huType != null) {
            waitingMap.put(item, huType.getValue());
        }
    }

    /**
     * 是不是可以回头杠
     *
     * @param item
     * @return
     */
    public boolean canHuiTouGang(MaItem item) {
        for (MaItem maItem : inMaItems)
            if (maItem.equals(item))
                return true;
        return false;
    }

    /**
     * 是不是可以杠
     * 包括暗杠和别人放的杠
     * @param item
     * @return
     */
    public boolean canGang(MaItem item)
    {
        if (getNumOfMaItem(item) > 2)
            return true;
        return false;
    }
    /**
     * 该玩家是不是可以碰
     *
     * @param item
     * @return
     */
    public boolean canPeng(MaItem item) {
        if (getNumOfMaItem(item) > 1)
            return true;
        return false;
    }

    /**
     * 该玩家具有该麻将的数量
     *
     * @param item
     * @return
     */
    private int getNumOfMaItem(MaItem item) {
        int s = 0;
        for (MaItem maItem : handMaItems) {
            if (maItem.equals(item))
                s++;
        }
        return s;
    }

    //是不是可以胡
    public HuType canHu(MaItem item) {
        if (handMaItems.size() != 13)
            throw new XiangGongException(String.format("您相公了,您有%s张牌", handMaItems.size()));
        insertMaItem(item);
        HuType score = isSuccess();
        playOut(item);
        return score;
    }

    //判断是否为东南西北风
    public boolean isFeng(MaItem item){
        return item.getType().getValue().equals("东")||
                item.getType().getValue().equals("南")||
                item.getType().getValue().equals("西")||
                item.getType().getValue().equals("北");
    }

    //判断是否为中发白
    public boolean isJian(MaItem item){
        return item.getType().getValue().equals("中")||
                item.getType().getValue().equals("發")||
                item.getType().getValue().equals("白");
    }



    //是不是可以吃
    public boolean canChi(List<MaItem> chiItem, MaItem item){
        if(isFeng(item) ||
                chiItem.size() != 2 ||
                isFeng(chiItem.get(0)) ||
                isFeng(chiItem.get(1)) ||
                isJian(item) && isJian(chiItem.get(0)) && isJian(chiItem.get(1)) ? false :
                chiItem.get(0).getType() != chiItem.get(1).getType()||
                chiItem.get(0).getType() != item.getType()){
            //风不能吃
            //不是两牌不能吃
            //花色不同不能吃
            return false;
        }

        List<MaItem> tempList = new ArrayList<>();
        tempList.addAll(chiItem);
        tempList.add(item);

        //判断是否为顺子
        return isShun(tempList);
    }

    //判断是否为顺子
    public boolean isShun(List<MaItem> itemList){
        List<MaItem> tempList = sorted(itemList);

        int pos = tempList.get(0).getPos();
        //如果都是剑,支持中发白可以吃
        if(isJian(tempList.get(0)) && isJian(tempList.get(1)) && isJian(tempList.get(2))){
            return pos + 10 == tempList.get(1).getPos() && pos + 20 == tempList.get(2).getPos();
        }
        //判断是否为顺子
        return pos + 1 == tempList.get(1).getPos() && pos + 2 == tempList.get(2).getPos();
    }

    //能不能胡
    public HuType isSuccess() {
        int s = hasOnePair(0);
        if (s == -1)//没有将，肯定没停牌
            return null;

        if (isOneColor()) {
            if (isSevenPair())
                return HuType.QING_SEVEN_PAIR;
            if (isPengPengHu())
                return HuType.PENG_QING_YI_SE;
            else {
                if (isNormalHu())
                    return HuType.QING_YI_SE;
            }
        } else {
            if (isSevenPair())
                return HuType.SEVEN_PAIR;
            if (isPengPengHu())
                return HuType.PENG_GPENG_HU;
            else {
                if (isNormalHu())
                    return HuType.NORMAL;
            }
        }
        return null;
    }

    //获取手中的对子
    public List<List<MaItem>> getPair(){
        return handMaItems
                .stream()
                .collect(Collectors.groupingBy(MaItem::toString))
                .entrySet()
                .stream()
                .filter(o->o.getValue().size()>1)
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparing(sort->sort.get(0).getPos()))
                .collect(Collectors.toList());
    }

    //一对坐将
    public int hasOnePair(int s) {
        for (int i = s; i < handMaItems.size() - 1; i++) {
            if (handMaItems.get(i).equals(handMaItems.get(i + 1)))
                return i + 1;
        }
        return -1;
    }

    //七对
    public boolean isSevenPair() {
        int pair = 0;
        for (int i = 0; i < handMaItems.size() - 1; i++) {
            if (handMaItems.get(i).equals(handMaItems.get(i + 1))) {
                pair++;
                i++;
            }
        }
        return pair == 7;
    }

    //清一色
    public boolean isOneColor() {
        MaType type = handMaItems.get(0).getType();
        //手里牌
        for (int i = 1; i < handMaItems.size(); i++) {
            if (handMaItems.get(i).getType() != type)
                return false;
        }
        //碰的牌
        for (int i = 0; i < inMaItems.size(); i++) {
            if (inMaItems.get(i).getType() != type)
                return false;
        }
        return true;
    }

    //碰碰胡
    public boolean isPengPengHu() {
        List<MaItem> maItems = new LinkedList<MaItem>();
        maItems.addAll(handMaItems);
        return isPairs(maItems);
    }

    //碰碰胡
    private boolean isPairs(List<MaItem> tempMaItems) {
        if (tempMaItems.size() == 2)
            return tempMaItems.get(0).equals(tempMaItems.get(1));
        if (tempMaItems.get(0).equals(tempMaItems.get(1)) && tempMaItems.get(0).equals(tempMaItems.get(2))) {
            tempMaItems.remove(0);
            tempMaItems.remove(0);
            tempMaItems.remove(0);
            return isPairs(tempMaItems);
        } else if (tempMaItems.get(0).equals(tempMaItems.get(1))) {
            tempMaItems.add(tempMaItems.get(0));
            tempMaItems.add(tempMaItems.get(1));
            tempMaItems.remove(0);
            tempMaItems.remove(0);
            return isPairs(tempMaItems);
        } else
            return false;
    }

    //正常胡牌
    public boolean isNormalHu() {
        List<MaItem> maItems = new LinkedList<MaItem>();
        maItems.addAll(handMaItems);
        return isNormalHu(maItems, false);
    }

    private boolean isNormalHu(List<MaItem> tempMaItems, boolean pair) {
        int s = 0;
        while (s > -1) {
            if (!pair) {
                s = hasOnePair(s);
            }
            if (s > -1) {
                List<MaItem> maItems = new LinkedList<MaItem>();
                maItems.addAll(tempMaItems);
                if (!pair) {
                    //将坐将移动到最后
                    maItems.add(maItems.get(s - 1));
                    maItems.add(maItems.get(s));
                    maItems.remove(s - 1);
                    maItems.remove(s - 1);
                }
                pair = true;
                if (hasOneHand(maItems))
                    return true;
                else {
                    pair = false;
                    continue;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    //是不是有一嘴，三个或者顺子
    private boolean hasOneHand(List<MaItem> tempMaItems) {
        if (tempMaItems.size() == 2) {
            return tempMaItems.get(0).equals(tempMaItems.get(1));
        }
        if (tempMaItems.get(0).equals(tempMaItems.get(1)) && tempMaItems.get(0).equals(tempMaItems.get(2))) {
            tempMaItems.remove(0);
            tempMaItems.remove(0);
            tempMaItems.remove(0);
            return hasOneHand(tempMaItems);
        }
        MaItem first = tempMaItems.get(0);
        for (int i = 1; i < tempMaItems.size(); i++) {
            if (tempMaItems.get(i).equals(first)) {
                continue;
            } else {
                MaItem secondItem = tempMaItems.get(i);//第二张牌
                if (first.getType() == secondItem.getType()) {
                    if (first.getValue().getIndex() + 1 == secondItem.getValue().getIndex()) {
                        for (int j = i + 1; j < tempMaItems.size(); j++) {
                            if (tempMaItems.get(j).equals(secondItem)) {
                                continue;
                            } else {
                                MaItem thirdMaItem = tempMaItems.get(j);//第三张牌
                                if (first.getType() == thirdMaItem.getType()) {
                                    if (secondItem.getValue().getIndex() + 1 == thirdMaItem.getValue().getIndex()) {
                                        tempMaItems.remove(j);
                                        tempMaItems.remove(i);
                                        tempMaItems.remove(0);
                                        return hasOneHand(tempMaItems);
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    //da打一张
    public void playOut(MaItem item) {
        outMaItems.add(item);
//        for (int i = 0; i < handMaItems.size(); i++) {
//            MaItem temp = handMaItems.get(i);
//            if (item.equals(temp)) {
//                handMaItems.remove(i);
//                break;
//            }
//        }
        handMaItems.remove(handMaItems.get(handMaItems.indexOf(item)));
    }

    /**
     * 是不是听牌
     *
     * @return
     */
    public boolean isWaiting() {
        //条
        return false;
    }

    //将拿到的牌插到合适的位置
    public void insertMaItem(MaItem item) {
//        if (handMaItems.size() == 0) {
//            handMaItems.add(item);
//        } else {
//            for (int i = 0; i < handMaItems.size(); i++) {
//                MaItem tempItem = handMaItems.get(i);
//                if (item.getPos() < tempItem.getPos()) {
//                    handMaItems.add(i, item);
//                    return;
//                }
//            }
//            handMaItems.add(item);
//        }
        handMaItems.add(item);
        handMaItems = sorted(handMaItems);
    }

    //整理牌的顺序
    public List<MaItem> sorted(List<MaItem> itemList){
        return itemList.stream()
                .sorted(Comparator.comparing(MaItem::getPos))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("玩家[").append(pid).append("]").append("\n")
                .append("手牌:");
        for (MaItem item : handMaItems) {
            sb.append(item.toString()).append(" ");
        }
        return sb.toString();
    }

    public MaItem getNewMaItem() {
        return newMaItem;
    }

    public void setNewMaItem(MaItem newMaItem) {
        this.newMaItem = newMaItem;
    }

    public boolean isPlayingOut() {
        return isPlayingOut;
    }

    public void setPlayingOut(boolean playingOut) {
        isPlayingOut = playingOut;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        isDealer = dealer;
    }

    public int getDealNum() {
        return dealNum;
    }

    public void setDealNum(int dealNum) {
        this.dealNum = dealNum;
    }

    public List<MaItem> getInitMaItems() {
        return initMaItems;
    }

    public void setInitMaItems(List<MaItem> initMaItems) {
        this.initMaItems = initMaItems;
    }

    public List<MaItem> getHandMaItems() {
        return handMaItems;
    }

    public void setHandMaItems(List<MaItem> handMaItems) {
        this.handMaItems = handMaItems;
    }

    public List<MaItem> getInMaItems() {
        return inMaItems;
    }

    public void setInMaItems(List<MaItem> inMaItems) {
        this.inMaItems = inMaItems;
    }

    public Stack<MaItem> getOutMaItems() {
        return outMaItems;
    }

    public void setOutMaItems(Stack<MaItem> outMaItems) {
        this.outMaItems = outMaItems;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public static Logger getLogger() {
        return logger;
    }

    public boolean isWatcher() {
        return isWatcher;
    }

    public void setWatcher(boolean watcher) {
        isWatcher = watcher;
    }

    public String getPid() {
        return pid;
    }

    public static void main(String[] args) {
        Player player = new Player("lip");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++)
                player.buildHand(new MaItem(MaType.WAN, MaValue.getMaValue(i + 1)));
        }
        player.buildHand(new MaItem(MaType.WAN, MaValue.getMaValue(7)));
        System.out.println(player);
        for (int i = 0; i < 9; i++) {
            MaItem item = new MaItem(MaType.WAN, MaValue.getMaValue(i + 1));
            System.out.print(item);
            System.out.println(" " + player.canHu(item));
        }
        System.out.println(player.canHu(new MaItem(MaType.FA)));
        System.out.println(player.getWaitingMaItem());
    }
}
