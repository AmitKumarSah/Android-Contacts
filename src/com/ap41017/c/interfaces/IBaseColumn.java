package com.ap41017.c.interfaces;

public interface IBaseColumn {

	public long getId();

	public <Ret, Arg> Ret accept(IVisitor<Ret, Arg> visitor, Arg arg);

}
