package com.ap41017.c.impl;

import com.ap41017.c.interfaces.ICallLog;
import com.ap41017.c.interfaces.IContact;
import com.ap41017.c.interfaces.IDataColumn.IGroupMembership;
import com.ap41017.c.interfaces.IDataColumn.IPhone;
import com.ap41017.c.interfaces.IGroup;
import com.ap41017.c.interfaces.IVisitor;

public class BasicVisitor<Ret, Arg> implements IVisitor<Ret, Arg> {

	@Override
	public Ret visit(IContact contact, Arg arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret visit(IGroup group, Arg arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret visit(IGroupMembership group, Arg arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret visit(IPhone phone, Arg arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret visit(ICallLog calllog, Arg arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
