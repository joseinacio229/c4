/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q106_ForgottenTruth;

import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.model.quest.State;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q106_ForgottenTruth extends Quest
{
	// NPCs
	private static final int THIFIELL = 7358;
	private static final int KARTIA = 7133;
	
	// Items
	private static final int ONYX_TALISMAN_1 = 984;
	private static final int ONYX_TALISMAN_2 = 985;
	private static final int ANCIENT_SCROLL = 986;
	private static final int ANCIENT_CLAY_TABLET = 987;
	private static final int KARTIA_TRANSLATION = 988;
	
	// Rewards
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int ELDRITCH_DAGGER = 989;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	private static final int LESSER_HEALING_POTION = 1060;
	
	public static void main(String[] args)
	{
		new Q106_ForgottenTruth();
	}
	
	public Q106_ForgottenTruth()
	{
		super(106, Q106_ForgottenTruth.class.getSimpleName(), "Forgotten Truth");
		
		setItemsIds(ONYX_TALISMAN_1, ONYX_TALISMAN_2, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET, KARTIA_TRANSLATION);
		
		addStartNpc(THIFIELL);
		addTalkId(THIFIELL, KARTIA);
		
		addKillId(5070); // Tumran Orc Brigand
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		String htmltext = event;
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("7358-05.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(ONYX_TALISMAN_1, 1);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case State.CREATED:
				if (player.getRace() != Race.DARK_ELF)
					htmltext = "7358-01.htm";
				else if (player.getLevel() < 10)
					htmltext = "7358-02.htm";
				else
					htmltext = "7358-03.htm";
				break;
			
			case State.STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case THIFIELL:
						if (cond == 1)
							htmltext = "7358-06.htm";
						else if (cond == 2)
							htmltext = "7358-06.htm";
						else if (cond == 3)
							htmltext = "7358-06.htm";
						else if (cond == 4)
						{
							htmltext = "7358-07.htm";
							st.takeItems(KARTIA_TRANSLATION, 1);
							st.giveItems(ELDRITCH_DAGGER, 1);
							st.giveItems(LESSER_HEALING_POTION, 100);
							
							if (player.isMageClass())
								st.giveItems(SPIRITSHOT_NO_GRADE, 500);
							else
								st.giveItems(SOULSHOT_NO_GRADE, 1000);
							
							if (player.isNewbie())
							{
								st.showQuestionMark(26);
								if (player.isMageClass())
								{
									st.playTutorialVoice("tutorial_voice_027");
									st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
								}
								else
								{
									st.playTutorialVoice("tutorial_voice_026");
									st.giveItems(SOULSHOT_FOR_BEGINNERS, 6000);
								}
							}
							
							st.giveItems(ECHO_BATTLE, 10);
							st.giveItems(ECHO_LOVE, 10);
							st.giveItems(ECHO_SOLITUDE, 10);
							st.giveItems(ECHO_FEAST, 10);
							st.giveItems(ECHO_CELEBRATION, 10);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case KARTIA:
						if (cond == 1)
						{
							htmltext = "7133-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ONYX_TALISMAN_1, 1);
							st.giveItems(ONYX_TALISMAN_2, 1);
						}
						else if (cond == 2)
							htmltext = "7133-02.htm";
						else if (cond == 3)
						{
							htmltext = "7133-03.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ONYX_TALISMAN_2, 1);
							st.takeItems(ANCIENT_SCROLL, 1);
							st.takeItems(ANCIENT_CLAY_TABLET, 1);
							st.giveItems(KARTIA_TRANSLATION, 1);
						}
						else if (cond == 4)
							htmltext = "7133-04.htm";
						break;
				}
				break;
			
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		final QuestState st = checkPlayerCondition(killer, npc, "cond", "2");
		if (st == null)
			return null;
		
		if (!st.hasQuestItems(ANCIENT_SCROLL))
			st.dropItems(ANCIENT_SCROLL, 1, 1, 200000);
		else if (st.dropItems(ANCIENT_CLAY_TABLET, 1, 1, 200000))
			st.set("cond", "3");
		
		return null;
	}
}
