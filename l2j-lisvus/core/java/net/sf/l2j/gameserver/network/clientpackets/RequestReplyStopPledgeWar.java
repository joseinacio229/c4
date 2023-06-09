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
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestReplyStopPledgeWar extends L2GameClientPacket
{
	private static final String _C__50_REQUESTREPLYSTOPPLEDGEWAR = "[C] 50 RequestReplyStopPledgeWar";
	// private static Logger _log = Logger.getLogger(RequestReplyStopPledgeWar.class.getName());

	private int _answer;

	@Override
	protected void readImpl()
	{
		readS();
		_answer = readD();
	}

	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		L2PcInstance requestor = activeChar.getActiveRequester();
		if (requestor == null)
		{
			return;
		}

		if (_answer == 1)
		{
			ClanTable.getInstance().deleteClanWars(requestor.getClanId(), activeChar.getClanId());
		}
		else
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.REQUEST_TO_END_WAR_HAS_BEEN_DENIED));
		}

		activeChar.setActiveRequester(null);
		requestor.onTransactionResponse();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.L2GameClientPacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__50_REQUESTREPLYSTOPPLEDGEWAR;
	}
}