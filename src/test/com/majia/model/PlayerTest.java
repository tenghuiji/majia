package com.majia.model;

import com.majia.enumerate.MaType;
import com.majia.enumerate.MaValue;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PlayerTest {
    Player player;
    @Before
    public void setUp() throws Exception {
        player = new Player("1001");
    }

    @Test
    public void clear() {
    }

    @Test
    public void getMaItemNum() {
    }

    @Test
    public void getActionsWhenOut() {
    }

    @Test
    public void initHandMaItem_4() {
    }

    @Test
    public void initHandMaItem_1() {
    }

    @Test
    public void getIn() {
    }

    @Test
    public void getInAtlast() {
    }

    @Test
    public void isActionConfirm() {
    }

    @Test
    public void setActionConfirm() {
    }

    @Test
    public void doPeng() {
    }

    @Test
    public void doGang() {
    }

    @Test
    public void doHuiTouGang() {
    }

    @Test
    public void getWaitingMaItem() {
    }

    @Test
    public void canHuiTouGang() {
    }

    @Test
    public void canGang() {
    }

    @Test
    public void canPeng() {
    }

    @Test
    public void canHu() {
    }

    @Test
    public void isFeng() {
    }

    @Test
    public void isZhongfabai() {
    }

    @Test
    public void canChi() {
        List<MaItem> maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.WAN, MaValue.EIGHT));
        maItemList.add(new MaItem(MaType.WAN, MaValue.FIVE));

        MaItem item = new MaItem(MaType.WAN,MaValue.EIGHT);

        assertEquals(false,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;

        maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.WAN, MaValue.EIGHT));
        maItemList.add(new MaItem(MaType.WAN, MaValue.SIX));

        item = new MaItem(MaType.WAN,MaValue.FIVE);

        assertEquals(false,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;

        maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.WAN, MaValue.EIGHT));
        maItemList.add(new MaItem(MaType.WAN, MaValue.SIX));

        item = new MaItem(MaType.WAN,MaValue.SEVEN);

        assertEquals(true,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;

        maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.DONG));
        maItemList.add(new MaItem(MaType.WAN, MaValue.SIX));

        item = new MaItem(MaType.WAN,MaValue.SEVEN);

        assertEquals(false,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;

        maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.DONG));
        maItemList.add(new MaItem(MaType.WAN, MaValue.SIX));

        item = new MaItem(MaType.BAI);

        assertEquals(false,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;

        maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.ZHONG));
        maItemList.add(new MaItem(MaType.BAI));

        item = new MaItem(MaType.FA);

        assertEquals(true,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;

        maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.DONG));
        maItemList.add(new MaItem(MaType.BAI));

        item = new MaItem(MaType.FA);

        assertEquals(false,player.canChi(maItemList,item));
        maItemList.clear();
        item = null;
    }

    @Test
    public void getPair(){
        List<MaItem> handMaItems = new ArrayList<>();
        handMaItems.add(new MaItem(MaType.WAN,MaValue.EIGHT));
        handMaItems.add(new MaItem(MaType.WAN,MaValue.EIGHT));
        handMaItems.add(new MaItem(MaType.WAN,MaValue.EIGHT));
        handMaItems.add(new MaItem(MaType.WAN,MaValue.EIGHT));
        handMaItems.add(new MaItem(MaType.TIAO,MaValue.THREE));
        handMaItems.add(new MaItem(MaType.TIAO,MaValue.THREE));
        handMaItems.add(new MaItem(MaType.TIAO,MaValue.THREE));
        handMaItems.add(new MaItem(MaType.TONG,MaValue.TWO));
        handMaItems.add(new MaItem(MaType.TONG,MaValue.TWO));
        handMaItems.add(new MaItem(MaType.DONG));
        player.setHandMaItems(handMaItems);
        List<List<MaItem>> pairs =  player.getPair();
        assertEquals(3,pairs.size());
        assertEquals(3,pairs.get(0).size());
        assertEquals(new MaItem(MaType.TIAO,MaValue.THREE),pairs.get(0).get(0));
        assertEquals(new MaItem(MaType.TIAO,MaValue.THREE),pairs.get(0).get(1));
        assertEquals(new MaItem(MaType.TIAO,MaValue.THREE),pairs.get(0).get(2));

        assertEquals(2,pairs.get(1).size());
        assertEquals(new MaItem(MaType.TONG,MaValue.TWO),pairs.get(1).get(0));
        assertEquals(new MaItem(MaType.TONG,MaValue.TWO),pairs.get(1).get(1));

        assertEquals(4,pairs.get(2).size());
        assertEquals(new MaItem(MaType.WAN,MaValue.EIGHT),pairs.get(2).get(0));
        assertEquals(new MaItem(MaType.WAN,MaValue.EIGHT),pairs.get(2).get(1));
        assertEquals(new MaItem(MaType.WAN,MaValue.EIGHT),pairs.get(2).get(2));
        assertEquals(new MaItem(MaType.WAN,MaValue.EIGHT),pairs.get(2).get(3));

        List<MaItem> list = Arrays.asList(
                new MaItem(MaType.WAN,MaValue.EIGHT),
                new MaItem(MaType.WAN,MaValue.EIGHT),
                new MaItem(MaType.TIAO,MaValue.THREE),
                new MaItem(MaType.WAN,MaValue.EIGHT),
                new MaItem(MaType.TIAO,MaValue.THREE),
                new MaItem(MaType.TONG,MaValue.TWO),
                new MaItem(MaType.TIAO,MaValue.THREE),
                new MaItem(MaType.TONG,MaValue.TWO),
                new MaItem(MaType.TIAO,MaValue.THREE),
                new MaItem(MaType.DONG),
                new MaItem(MaType.DONG),
                new MaItem(MaType.FA)
        );
        List<List<MaItem>> res =
        list.stream()
                .collect(Collectors.groupingBy(MaItem::toString))
                .entrySet()
                .stream()
                .filter(o->o.getValue().size()>1)
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparing(o->o.size()))
                .collect(Collectors.toList());
        assertEquals(4,res.size());

    }
    @Test
    public void isSuccess() {
    }

    @Test
    public void hasOnePair() {
    }

    @Test
    public void isSevenPair() {
    }

    @Test
    public void isOneColor() {
    }

    @Test
    public void isPengPengHu() {
    }

    @Test
    public void isNormalHu() {
    }

    @Test
    public void playOut() {
    }

    @Test
    public void isWaiting() {
    }

    @Test
    public  void insertMaItem(){
        List<MaItem> maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.WAN, MaValue.EIGHT));
        maItemList.add(new MaItem(MaType.WAN, MaValue.FIVE));
        maItemList.add(new MaItem(MaType.WAN, MaValue.ONE));
        player.setHandMaItems(player.sorted(maItemList));

        player.insertMaItem(new MaItem(MaType.TIAO,MaValue.FOUR));

        List<MaItem> newHandsItems = player.getHandMaItems();

        assertEquals(newHandsItems.get(0).getType(),MaType.TIAO);
        assertEquals(newHandsItems.get(0).getValue(),MaValue.FOUR);

        assertEquals(newHandsItems.get(1).getType(),MaType.WAN);
        assertEquals(newHandsItems.get(1).getValue(),MaValue.ONE);

        assertEquals(newHandsItems.get(2).getType(),MaType.WAN);
        assertEquals(newHandsItems.get(2).getValue(),MaValue.FIVE);

        assertEquals(newHandsItems.get(3).getType(),MaType.WAN);
        assertEquals(newHandsItems.get(3).getValue(),MaValue.EIGHT);

    }
    @Test
    public void sorted() {
        List<MaItem> maItemList = new ArrayList<>();
        maItemList.add(new MaItem(MaType.WAN, MaValue.EIGHT));
        maItemList.add(new MaItem(MaType.WAN, MaValue.FIVE));
        maItemList.add(new MaItem(MaType.WAN, MaValue.ONE));
        maItemList = player.sorted(maItemList);

        assertEquals(maItemList.get(0).getType(),MaType.WAN);
        assertEquals(maItemList.get(0).getValue(),MaValue.ONE);

        assertEquals(maItemList.get(1).getType(),MaType.WAN);
        assertEquals(maItemList.get(1).getValue(),MaValue.FIVE);

        assertEquals(maItemList.get(2).getType(),MaType.WAN);
        assertEquals(maItemList.get(2).getValue(),MaValue.EIGHT);
    }

    @Test
    public void testToString() {
    }

    @Test
    public void getNewMaItem() {
    }

    @Test
    public void setNewMaItem() {
    }

    @Test
    public void isPlayingOut() {
    }

    @Test
    public void setPlayingOut() {
    }

    @Test
    public void getDirection() {
    }

    @Test
    public void setDirection() {
    }

    @Test
    public void getScore() {
    }

    @Test
    public void setScore() {
    }

    @Test
    public void isDealer() {
    }

    @Test
    public void setDealer() {
    }

    @Test
    public void getDealNum() {
    }

    @Test
    public void setDealNum() {
    }

    @Test
    public void getInitMaItems() {
    }

    @Test
    public void setInitMaItems() {
    }

    @Test
    public void getHandMaItems() {
    }

    @Test
    public void setHandMaItems() {
    }

    @Test
    public void getInMaItems() {
    }

    @Test
    public void setInMaItems() {
    }

    @Test
    public void getOutMaItems() {
    }

    @Test
    public void setOutMaItems() {
    }

    @Test
    public void setPid() {
    }

    @Test
    public void getLogger() {
    }

    @Test
    public void isWatcher() {
    }

    @Test
    public void setWatcher() {
    }

    @Test
    public void getPid() {
    }

    @Test
    public void main() {
    }
}