package com.ak4.dao;

import com.ak4.data.Emp;
import rx.Observable;

public interface EmpManagerDAO {

    Observable<Emp> getEmpByEmpId(String empId);
}
