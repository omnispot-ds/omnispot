package com.kesdip.player.components.flash.shockwaveflashobjects.impl;

import com.jniwrapper.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.*;

/**
 * Represents COM interface IFlashFactory.
 */
public class IFlashFactoryImpl extends IUnknownImpl
    implements IFlashFactory
{
    public static final String INTERFACE_IDENTIFIER = IFlashFactory.INTERFACE_IDENTIFIER;
    private static final IID _iid = IID.create(INTERFACE_IDENTIFIER);

    public IFlashFactoryImpl()
    {
    }

    protected IFlashFactoryImpl(IUnknownImpl that) throws ComException
    {
        super(that);
    }

    public IFlashFactoryImpl(IUnknown that) throws ComException
    {
        super(that);
    }

    public IFlashFactoryImpl(CLSID clsid, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, dwClsContext);
    }

    public IFlashFactoryImpl(CLSID clsid, IUnknownImpl pUnkOuter, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, pUnkOuter, dwClsContext);
    }

    public IID getIID()
    {
        return _iid;
    }

    public Object clone()
    {
        return new IFlashFactoryImpl(this);
    }
}
