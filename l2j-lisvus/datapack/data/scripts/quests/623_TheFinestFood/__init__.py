# The Finest Food - v0.1 by disKret & DrLecter (adapted for L2JLisvus by roko91)

import sys
from net.sf.l2j import Config
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "623_TheFinestFood"

#NPC
JEREMY = 8521

#ITEMS
LEAF_OF_FLAVA,BUFFALO_MEAT,ANTELOPE_HORN = range(7199,7202)

#MOBS, DROPS, CHANCES & REWARDS
BUFFALO,FLAVA,ANTELOPE = [ 1315,1316,1318 ]
DROPLIST = {BUFFALO:[BUFFALO_MEAT,99],FLAVA:[LEAF_OF_FLAVA,99],ANTELOPE:[ANTELOPE_HORN,99]}
REWARDS = [[6849,25000,0,11],[6847,65000,12,23],[6851,25000,24,33],[0,73000,34,100]]

#needed count

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = range(7199,7202)

 def onEvent (self,event,st) :
   cond = st.getInt("cond")
   htmltext = event
   leaf = st.getQuestItemsCount(LEAF_OF_FLAVA)
   meat = st.getQuestItemsCount(BUFFALO_MEAT)
   horn = st.getQuestItemsCount(ANTELOPE_HORN)
   if event == "8521-03.htm" and cond == 0 :
     if st.getPlayer().getLevel() >= 71 :
        st.set("cond","1")
        st.setState(State.STARTED)
        st.playSound("ItemSound.quest_accept")
     else :
        htmltext = "8521-02.htm"
        st.exitQuest(1)
   elif event == "8521-07.htm" :
     if cond == 2 and leaf == meat == horn == 100 :
        htmltext = "8521-06.htm"
        st.playSound("ItemSound.quest_finish")
        random = st.getRandom(100)
        i = 0
        while i < len(REWARDS) :
            item,adena,chance,chance2=REWARDS[i]
            if chance<=random<= chance2 :
              break
            i = i+1
        st.giveItems(57,adena)
        if item :
           st.giveItems(item,1)
        else :
           st.addExpAndSp(230000,18250)
        st.takeItems(LEAF_OF_FLAVA,-1)
        st.takeItems(BUFFALO_MEAT,-1)
        st.takeItems(ANTELOPE_HORN,-1)
        st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,st) :
   htmltext = JQuest.getNoQuestMsg()
   if st :
       cond = st.getInt("cond")
       leaf = st.getQuestItemsCount(LEAF_OF_FLAVA)
       meat = st.getQuestItemsCount(BUFFALO_MEAT)
       horn = st.getQuestItemsCount(ANTELOPE_HORN)
       if cond == 0 :
          htmltext = "8521-01.htm"
       elif st.getState() == State.STARTED :
           if cond == 1 :
              htmltext = "8521-05.htm"
           elif cond == 2 and leaf == meat == horn == 100 :
              htmltext = "8521-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   # todo: with the current code, a player who has completed up to 2 out of 3
   # item collections may consume the party drop (i.e. become the selected
   # player in the random, but get nothing because it was the wrong mob)
   # this ought to be corrected later...
   partyMember = self.getRandomPartyMember(player,npc,"1")
   if not partyMember: return
   st = partyMember.getQuestState(qn)
   if st :
      if st.getState() == State.STARTED :
         item,chance = DROPLIST[npc.getNpcId()]
         count = st.getQuestItemsCount(item)
         if st.getInt("cond") == 1 and count < 100 :
            numItems, chance = divmod(chance*Config.RATE_DROP_QUEST,100)
            if st.getRandom(100) < chance :
               numItems += 1
            if count + numItems >= 100 :
              numItems = 100 - count
            if numItems != 0 :
              st.giveItems(item,int(numItems))
              if st.getQuestItemsCount(LEAF_OF_FLAVA) == st.getQuestItemsCount(BUFFALO_MEAT) == st.getQuestItemsCount(ANTELOPE_HORN) == 100 :
                 st.set("cond","2")
                 st.playSound("ItemSound.quest_middle")
              else :
                 st.playSound("ItemSound.quest_itemget")
   return

QUEST = Quest(623,qn,"The Finest Food")
QUEST.addStartNpc(JEREMY)

QUEST.addTalkId(JEREMY)

for mob in DROPLIST.keys() :
  QUEST.addKillId(mob)