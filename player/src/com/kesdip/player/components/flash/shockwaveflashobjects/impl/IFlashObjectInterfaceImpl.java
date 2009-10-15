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
 * Represents COM interface IFlashObjectInterface.
 */
public class IFlashObjectInterfaceImpl extends IDispatchExImpl
    implements IFlashObjectInterface
{
    public static final String INTERFACE_IDENTIFIER = IFlashObjectInterface.INTERFACE_IDENTIFIER;
    private static final IID _iid = IID.create(INTERFACE_IDENTIFIER);

    public IFlashObjectInterfaceImpl()
    {
    }

    protected IFlashObjectInterfaceImpl(IUnknownImpl that) throws ComException
    {
        super(that);
    }

    public IFlashObjectInterfaceImpl(IUnknown that) throws ComException
    {
        super(that);
    }

    public IFlashObjectInterfaceImpl(CLSID clsid, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, dwClsContext);
    }

    public IFlashObjectInterfaceImpl(CLSID clsid, IUnknownImpl pUnkOuter, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, pUnkOuter, dwClsContext);
    }

    public IID getIID()
    {
        return _iid;
    }

    public Object clone()
    {
        return new IFlashObjectInterfaceImpl(this);
    }
}
