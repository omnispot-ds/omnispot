package com.kesdip.player.components.flash.shockwaveflashobjects.server;

import com.jniwrapper.*;
import com.jniwrapper.win32.*;
import com.jniwrapper.win32.automation.*;
import com.jniwrapper.win32.automation.impl.*;
import com.jniwrapper.win32.automation.server.*;
import com.jniwrapper.win32.automation.types.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.server.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.impl.*;

/**
 * Represents VTBL for COM interface IFlashObjectInterface.
 */
public class IFlashObjectInterfaceVTBL extends IDispatchExVTBL
{
    public IFlashObjectInterfaceVTBL(CoClassMetaInfo classMetaInfo)
    {
        super(classMetaInfo);

        addMembers(
            new VirtualMethodCallback[] {
            }
        );
    }
}