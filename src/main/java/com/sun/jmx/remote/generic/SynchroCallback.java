/*
 * @(#)file      SynchroCallback.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.3
 * @(#)lastedit  07/03/08
 * @(#)build     @BUILD_TAG_PLACEHOLDER@
 *
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 2007 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL")(collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://opendmk.dev.java.net/legal_notices/licenses.txt or in the 
 * LEGAL_NOTICES folder that accompanied this code. See the License for the 
 * specific language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file found at
 *     http://opendmk.dev.java.net/legal_notices/licenses.txt
 * or in the LEGAL_NOTICES folder that accompanied this code.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.
 * 
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * 
 *       "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding
 * 
 *       "[Contributor] elects to include this software in this distribution
 *        under the [CDDL or GPL Version 2] license."
 * 
 * If you don't indicate a single choice of license, a recipient has the option
 * to distribute your version of this file under either the CDDL or the GPL
 * Version 2, or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the
 * GPL Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 * 
 */ 


package com.sun.jmx.remote.generic;

import java.io.IOException;

import javax.management.remote.message.Message;
import javax.management.remote.generic.*;

/**
 * This interface specifies methods to call back a server to execute a remote request.
 */
public interface SynchroCallback {

    /**
     * Called when receiving a remote request.
     * If the return value is null, then an implementation does not need to send back this null value to the other side. This is the case when receiving messages sent by the <code>sendOneWay</code> method.
     * For example, a client will send <code>CloseMessage</code> using the <code>sendOneWay</code> method and a server receiving this message should return null.
     */
    Message execute(Message request);

    /**
     * Called when an Exception occurs when reading a request sent by the peer or when writing the response to the recived request.
     */
    void connectionException(Exception ie);
}
