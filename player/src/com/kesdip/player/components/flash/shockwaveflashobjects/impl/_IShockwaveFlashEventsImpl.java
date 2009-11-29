package com.kesdip.player.components.flash.shockwaveflashobjects.impl;

import com.jniwrapper.*;
import com.jniwrapper.win32.*;
import com.jniwrapper.win32.automation.*;
import com.jniwrapper.win32.automation.impl.*;
import com.jniwrapper.win32.automation.types.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.*;

/**
 * Represents COM dispinterface _IShockwaveFlashEvents.
 */
public class _IShockwaveFlashEventsImpl extends IDispatchImpl
    implements _IShockwaveFlashEvents
{
    public static final String INTERFACE_IDENTIFIER = "{D27CDB6D-AE6D-11CF-96B8-444553540000}";
    private static final IID _iid = IID.create(INTERFACE_IDENTIFIER);

    public _IShockwaveFlashEventsImpl()
    {
    }

    protected _IShockwaveFlashEventsImpl(IUnknownImpl that) throws ComException
    {
        super(that);
    }

    public _IShockwaveFlashEventsImpl(IUnknown that) throws ComException
    {
        super(that);
    }

    public _IShockwaveFlashEventsImpl(CLSID clsid, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, dwClsContext);
    }

    public _IShockwaveFlashEventsImpl(CLSID clsid, IUnknownImpl pUnkOuter, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, pUnkOuter, dwClsContext);
    }

    public IID getIID()
    {
        return _iid;
    }

    public Object clone()
    {
        return new _IShockwaveFlashEventsImpl(this);
    }
}
