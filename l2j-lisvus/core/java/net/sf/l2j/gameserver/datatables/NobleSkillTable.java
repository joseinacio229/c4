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
package net.sf.l2j.gameserver.datatables;

import net.sf.l2j.gameserver.model.L2Skill;

/**
 *
 * @author -Nemesiss-
 */
public class NobleSkillTable
{
	public static NobleSkillTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private L2Skill[] _nobleSkills;

	private NobleSkillTable()
	{
		_nobleSkills = new L2Skill[8];
		_nobleSkills[0] = SkillTable.getInstance().getInfo(1323, 1);
		_nobleSkills[1] = SkillTable.getInstance().getInfo(325, 1);
		_nobleSkills[2] = SkillTable.getInstance().getInfo(326, 1);
		_nobleSkills[3] = SkillTable.getInstance().getInfo(327, 1);
		_nobleSkills[4] = SkillTable.getInstance().getInfo(1324, 1);
		_nobleSkills[5] = SkillTable.getInstance().getInfo(1325, 1);
		_nobleSkills[6] = SkillTable.getInstance().getInfo(1326, 1);
		_nobleSkills[7] = SkillTable.getInstance().getInfo(1327, 1);
	}

	public L2Skill[] getNobleSkills()
	{
		return _nobleSkills;
	}
	
	private static class SingletonHolder
	{
		protected static final NobleSkillTable _instance = new NobleSkillTable();
	}
}