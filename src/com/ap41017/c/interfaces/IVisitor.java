package com.ap41017.c.interfaces;

public interface IVisitor<Ret, Arg> {

	public Ret visit(IContact contact, Arg arg);

	public Ret visit(IGroup group, Arg arg);

	public Ret visit(IDataColumn.IGroupMembership group, Arg arg);

	public Ret visit(IDataColumn.IPhone phone, Arg arg);

	public Ret visit(ICallLog calllog, Arg arg);
}
