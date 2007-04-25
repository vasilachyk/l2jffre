package net.sf.l2j.gameserver.clientpackets;

import java.nio.ByteBuffer;

import net.sf.l2j.gameserver.L2GameClient;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class RequestReplySurrenderPledgeWar extends ClientBasePacket
{
    private static final String _C__52_REQUESTREPLYSURRENDERPLEDGEWAR = "[C] 52 RequestReplySurrenderPledgeWar";
    //private final static Log _log = LogFactory.getLog(RequestReplySurrenderPledgeWar.class.getName());

    int _answer;
    L2Clan _clan;
    L2PcInstance player;
    
    public RequestReplySurrenderPledgeWar(ByteBuffer buf, L2GameClient client)
    {
        super(buf, client);
        @SuppressWarnings("unused") String _reqName = readS();
        _answer  = readD();
    }

    void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        L2PcInstance requestor = activeChar.getActiveRequester();
        if (requestor == null)
            return;
        
        if (_answer == 1)
        {
            requestor.deathPenalty(false);
            ClanTable.getInstance().deleteclanswars(requestor.getClanId(), activeChar.getClanId());            
        } 
        else
        {
        }
        
        activeChar.onTransactionRequest(null);
    }

    public String getType()
    {
        return _C__52_REQUESTREPLYSURRENDERPLEDGEWAR;
    }
}