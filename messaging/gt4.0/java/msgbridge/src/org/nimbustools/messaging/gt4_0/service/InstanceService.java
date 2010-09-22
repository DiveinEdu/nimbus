/*
 * Copyright 1999-2008 University of Chicago
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.nimbustools.messaging.gt4_0.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.security.SecurityManager;
import org.nimbustools.api.services.rm.DoesNotExistException;
import org.nimbustools.api.services.rm.OperationDisabledException;
import org.nimbustools.messaging.gt4_0.common.CommonUtil;
import org.nimbustools.messaging.gt4_0.generated.types.OperationDisabledFault;
import org.nimbustools.messaging.gt4_0.generated.types.ShutdownEnumeration;
import org.nimbustools.messaging.gt4_0.generated.types.VoidType;
import org.nimbustools.messaging.gt4_0.generated.types.WorkspaceShutdownFault;
import org.nimbustools.messaging.gt4_0.generated.types.WorkspaceShutdownRequest_Type;
import org.nimbustools.messaging.gt4_0.generated.types.WorkspaceStartFault;
import org.nimbustools.messaging.gt4_0.generated.types.WorkspaceUnknownFault;
import org.nimbustools.messaging.gt4_0.FaultUtil;

public class InstanceService {

    // -------------------------------------------------------------------------
    // STATIC VARIABLES
    // -------------------------------------------------------------------------

    private static final Log logger =
            LogFactory.getLog(InstanceService.class.getName());


    // -------------------------------------------------------------------------
    // HELPER
    // -------------------------------------------------------------------------

    protected InstanceResource getInstanceResource()
            throws ResourceException, ResourceContextException {
        final ResourceContext context =
                    ResourceContext.getResourceContext();
        return (InstanceResource)context.getResource();
    }
    

    // -------------------------------------------------------------------------
    // methods corresponding to WorkspacePortType
    // -------------------------------------------------------------------------

    public VoidType start(VoidType none) throws WorkspaceStartFault,
                                                OperationDisabledFault,
                                                WorkspaceUnknownFault {

        final String callerDN = SecurityManager.getManager().getCaller();

        if (callerDN == null) {
            logger.error("VWS-START: no caller identity");
            throw InstanceUtil.makeStartFault("no caller identity", null);
        }

        try {
            final InstanceResource resource = getInstanceResource();
            resource.start(callerDN);
        } catch (DoesNotExistException e) {
            final String err = CommonUtil.genericExceptionMessageWrapper(e);
            throw InstanceUtil.makeUnknownFault(err);
        } catch (NoSuchResourceException e) {
            final String err = CommonUtil.genericExceptionMessageWrapper(e);
            throw InstanceUtil.makeUnknownFault(err);
        } catch (OperationDisabledException e) {
            logger.error(e);
            throw InstanceUtil.makeDisabledFault(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e);
            throw InstanceUtil.makeStartFault(e.getMessage(), e);
        } catch (Throwable t) {
            throw InstanceUtil.makeStartFault(
                    FaultUtil.unknown(t, "service.start"), null);
        }

        return new VoidType();
    }

    public VoidType shutdown(WorkspaceShutdownRequest_Type req)
                                    throws WorkspaceShutdownFault,
                                           OperationDisabledFault,
                                           WorkspaceUnknownFault {

        final String callerDN = SecurityManager.getManager().getCaller();

        if (callerDN == null) {
            logger.error("VWS-SHUTDOWN: no caller identity");
            throw InstanceUtil.makeShutdownFault("no caller identity", null);
        }

        final ShutdownEnumeration shutdownEnum = req.getShutdownType();

        try {

            final InstanceResource resource = getInstanceResource();

            resource.shutdown(shutdownEnum, req.getPostShutdown(),
                              false, callerDN);

        } catch (DoesNotExistException e) {
            final String err = CommonUtil.genericExceptionMessageWrapper(e);
            throw InstanceUtil.makeUnknownFault(err);
        } catch (NoSuchResourceException e) {
            final String err = CommonUtil.genericExceptionMessageWrapper(e);
            throw InstanceUtil.makeUnknownFault(err);
        } catch (OperationDisabledException e) {
            logger.error(e);
            throw InstanceUtil.makeDisabledFault(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e);
            throw InstanceUtil.makeShutdownFault(e.getMessage(), e);
        } catch (Throwable t) {
            throw InstanceUtil.makeShutdownFault(
                    FaultUtil.unknown(t, "service.shutdown"), null);
        }

        return new VoidType();
    }
}
