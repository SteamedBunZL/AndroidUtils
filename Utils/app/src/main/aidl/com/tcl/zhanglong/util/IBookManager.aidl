// IBookManager.aidl
package com.tcl.zhanglong.util;

import com.tcl.zhanglong.utils.IPC.Book;

// Declare any non-default types here with import statements

interface IBookManager {

   List<Book> getBookList();
    void addBook(in Book book);
}
