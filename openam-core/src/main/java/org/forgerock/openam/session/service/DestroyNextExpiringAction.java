/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt See the License for the specific language
 * governing permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at opensso/legal/CDDLv1.0.txt. If applicable,
 * add the following below the CDDL Header, with the fields enclosed by brackets
 * [] replaced by your own identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Portions Copyrighted 2011-2015 ForgeRock AS.
 */
package org.forgerock.openam.session.service;

import static org.forgerock.openam.session.SessionConstants.*;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.iplanet.dpro.session.Session;
import com.iplanet.dpro.session.SessionException;
import com.iplanet.dpro.session.SessionID;
import com.iplanet.dpro.session.service.InternalSession;
import com.iplanet.dpro.session.service.QuotaExhaustionAction;
import com.sun.identity.shared.debug.Debug;
import java.util.Map;
import javax.inject.Inject;
import org.forgerock.guice.core.InjectorHolder;
import org.forgerock.openam.session.SessionCache;

public class DestroyNextExpiringAction implements QuotaExhaustionAction {

    private static Debug debug = InjectorHolder.getInstance(Key.get(Debug.class, Names.named(SESSION_DEBUG)));

    private final SessionCache sessionCache;

    public DestroyNextExpiringAction() {
        this.sessionCache = InjectorHolder.getInstance(SessionCache.class);
    }

    @Inject
    public DestroyNextExpiringAction(SessionCache sessionCache) {
        this.sessionCache = sessionCache;
    }

    @Override
    public boolean action(InternalSession is, Map<String, Long> sessions) {

        String nextExpiringSessionID = null;
        long smallestExpTime = Long.MAX_VALUE;
        for (Map.Entry<String, Long> entry : sessions.entrySet()) {
            String sid = entry.getKey();
            long expirationTime = entry.getValue();
            if (expirationTime < smallestExpTime) {
                smallestExpTime = expirationTime;
                nextExpiringSessionID = sid;
            }

        }
        if (nextExpiringSessionID != null) {
            SessionID sessID = new SessionID(nextExpiringSessionID);
            try {
                Session s = sessionCache.getSession(sessID);
                s.destroySession(s);
            } catch (SessionException e) {
                if (debug.messageEnabled()) {
                    debug.message("Failed to destroy the next "
                            + "expiring session.", e);
                }
                // deny the session activation request
                // in this case
                return true;
            }
        }
        return false;
    }
}
