/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese
 * opensource volunteers. you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Any questions about this component can be directed to it's project Web address
 * https://code.google.com/p/opencloudb/.
 *
 */
package com.actiontech.dble.manager.response;

import com.actiontech.dble.DbleServer;
import com.actiontech.dble.backend.datasource.PhysicalDBPool;
import com.actiontech.dble.manager.ManagerConnection;
import com.actiontech.dble.net.mysql.OkPacket;
import com.actiontech.dble.route.parser.ManagerParseStop;
import com.actiontech.dble.route.parser.util.Pair;
import com.actiontech.dble.util.FormatUtil;
import com.actiontech.dble.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * StopHeartbeatCheck
 *
 * @author mycat
 */
public final class StopHeartbeat {
    private StopHeartbeat() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StopHeartbeat.class);

    public static void execute(String stmt, ManagerConnection c) {
        int count = 0;
        Pair<String[], Integer> keys = ManagerParseStop.getPair(stmt);
        if (keys.getKey() != null && keys.getValue() != null) {
            long time = keys.getValue() * 1000L;
            Map<String, PhysicalDBPool> dns = DbleServer.getInstance().getConfig().getDataHosts();
            for (String key : keys.getKey()) {
                PhysicalDBPool dn = dns.get(key);
                if (dn != null) {
                    dn.getSource().setHeartbeatRecoveryTime(TimeUtil.currentTimeMillis() + time);
                    ++count;
                    StringBuilder s = new StringBuilder();
                    s.append(dn.getHostName()).append(" stop heartbeat '");
                    LOGGER.warn(s.append(FormatUtil.formatTime(time, 3)).append("' by manager.").toString());
                }
            }
        }
        OkPacket packet = new OkPacket();
        packet.setPacketId(1);
        packet.setAffectedRows(count);
        packet.setServerStatus(2);
        packet.write(c);
    }

}