// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util;


/**
 * Builder is a creational design pattern that lets you construct complex objects step by step. The
 * pattern allows you to produce different types and representations of an object using the same
 * construction code.
 * 
 * @see https://refactoring.guru/design-patterns/builder
 */
public interface Builder<T> {

  T build();
}
