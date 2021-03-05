/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import static java.lang.System.arraycopy;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * 最后更新日期:2003年3月11日
 * 数组操作工具类，提供常见的数组操作需要的方法。
 * @author chenYuan
 */
public class ArrayUtil {
    public static final String[] emptyString = new String[0];

    //public static final int[] emptyInt = new int[0];

    public static final int INDEX_NOT_FOUND = -1;

    public static final Object[] NULL = null;

    /**
     * 私有构造方法，防止类的实例化，因为工具类不需要实例化。
     */
    private ArrayUtil() {

    }

    //-----------------------------------------------------------------------

    /**
     * Shallow clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * The objects in the array are not cloned, thus there is no special
     * handling for multi-dimensional arrays.
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer shallow clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static Object[] clone(Object[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static long[] clone(long[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static int[] clone(int[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static short[] clone(short[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static char[] clone(char[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static double[] clone(double[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static float[] clone(float[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * Clones an array returning a typecast result and handling
     * {@code null  } .
     * <p>
     * This method returns {@code null  } for a {@code null  } input array.
     *
     * @param array the array transfer clone, may be {@code null  }
     * @return the cloned array, {@code null  } if {@code null  } input
     */
    public static boolean[] clone(boolean[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    // ----------------------------------------------------------------------
    public static boolean isEmpty(String[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of Objects is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0 || array[0] == null;
    }

    /**
     * Checks if an array of primitive longs is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive ints is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive shorts is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive chars is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive bytes is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive doubles is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive floats is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of primitive booleans is empty or {@code null  } .
     *
     * @param array the array transfer testaio
     * @return [code]true } if the array is empty or {@code null  }
     * @since 2.1
     */
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(null, null)     = null
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * ArrayUtils.addAll([null], [null]) = [null, null]
     * ArrayUtils.addAll(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array, may be {@code null  }
     * @param array2 the second array whose elements are added transfer the new array, may be {@code null  }
     * @return The new array, {@code null  } if {@code null  } array inputs.
     * The type of the new array is the type of the first array.
     * @since 2.1
     */
    public static Object[] join(Object[] array1, Object[] array2) {
        if (array1 == null) {
            return ArrayUtil.clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        Object[] joinedArray = (Object[]) Array.newInstance(array1.getClass().getComponentType(),
                array1.length + array2.length);
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new boolean[] array.
     * @since 2.1
     */
    public static boolean[] join(boolean[] array1, boolean[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        boolean[] joinedArray = new boolean[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new char[] array.
     * @since 2.1
     */
    public static char[] join(char[] array1, char[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        char[] joinedArray = new char[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new byte[] array.
     * @since 2.1
     */
    public static byte[] join(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new short[] array.
     * @since 2.1
     */
    public static short[] join(short[] array1, short[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        short[] joinedArray = new short[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new int[] array.
     * @since 2.1
     */
    public static int[] join(int[] array1, int[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        int[] joinedArray = new int[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new long[] array.
     * @since 2.1
     */
    public static long[] join(long[] array1, long[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        long[] joinedArray = new long[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static Long[] join(Long[] array1, Long[] array2) {
        if (array1 == null) {
            return array2;
        } else if (array2 == null) {
            return array1;
        }
        Long[] joinedArray = new Long[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new float[] array.
     * @since 2.1
     */
    public static float[] join(float[] array1, float[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        float[] joinedArray = new float[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * Adds all the elements of the given arrays into a new array.
     * The new array contains all of the element of [code]array1 } followed
     * by all of the elements [code]array2 } . When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added transfer the new array.
     * @param array2 the second array whose elements are added transfer the new array.
     * @return The new double[] array.
     * @since 2.1
     */
    public static double[] join(double[] array1, double[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        double[] joinedArray = new double[array1.length + array2.length];
        arraycopy(array1, 0, joinedArray, 0, array1.length);
        arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     *
     * @param array 数组
     * @param element 类型
     * @return 类型
     */
    public static Class<?>[] add(Class<?>[] array, Class<?> element) {
        if (array == null) {
            array = new Class[1];
            array[0] = element;
            return array;
        }
        Class<?>[] newArray = (Class<?>[]) copyArrayGrow1(array, Class.class);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is {@code null  } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, null)      = [null]
     * ArrayUtils.add(null, "a")       = ["a"]
     * ArrayUtils.add(["a"], null)     = ["a", null]
     * ArrayUtils.add(["a"], "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param array   the array transfer "add" the element transfer, may be {@code null  }
     * @param element the object transfer add
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static Object[] add(Object[] array, Object element) {
        if (array == null) {
            array = new Object[1];
            array[0] = element;
            return array;
        }
        Class<?> type = element != null ? element.getClass() : Object.class;
        Object[] newArray = (Object[]) copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is {@code null  } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, true)          = [true]
     * ArrayUtils.add([true], false)       = [true, false]
     * ArrayUtils.add([true, false], true) = [true, false, true]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be {@code null  }
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static boolean[] add(boolean[] array, boolean element) {
        boolean[] newArray = (boolean[]) copyArrayGrow1(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is {@code null  } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be {@code null  }
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static byte[] add(byte[] array, byte element) {
        byte[] newArray = (byte[]) copyArrayGrow1(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is {@code null  } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, '0')       = ['0']
     * ArrayUtils.add(['1'], '0')      = ['1', '0']
     * ArrayUtils.add(['1', '0'], '1') = ['1', '0', '1']
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be {@code null  }
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static char[] add(char[] array, char element) {
        char[] newArray = (char[]) copyArrayGrow1(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is {@code null  } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be {@code null  }
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static double[] add(double[] array, double element) {
        double[] newArray = (double[]) copyArrayGrow1(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static Double[] add(Double[] array, Double element) {
        Double[] newArray = (Double[]) copyArrayGrow1(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be [code]null [/code]
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static float[] add(float[] array, float element) {
        float[] newArray = (float[]) copyArrayGrow1(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static Float[] add(Float[] array, Float element) {
        Float[] newArray = (Float[]) copyArrayGrow1(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be [code]null [/code]
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static int[] add(int[] array, int element) {
        if (array == null) {
            array = new int[0];
        }
        int[] newArray = (int[]) copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static Integer[] add(Integer[] array, Integer element) {
        if (array == null) {
            array = new Integer[0];
        }
        Integer[] newArray = (Integer[]) copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be [code]null [/code]
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static long[] add(long[] array, long element) {
        long[] newArray = (long[]) copyArrayGrow1(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static Long[] add(Long[] array, Long element) {
        if (element == null) {
            return array;
        }
        if (array == null) {
            array = new Long[0];
        }
        Long[] newArray = new Long[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }

    /**
     * Copies the given array and adds the given element at the end of the new array.
     * <p>
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array transfer copy and add the element transfer, may be [code]null [/code]
     * @param element the object transfer add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static short[] add(short[] array, short element) {
        short[] newArray = (short[]) copyArrayGrow1(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * @param array   字符串
     * @param element 添加内容
     * @return String[] 追加一条在 String[] 最后一个
     */
    public static String[] add(String[] array, String element) {
        if (array == null) {
            array = new String[1];
            array[0] = element;
            return array;
        }
        String[] result = new String[array.length + 1];
        arraycopy(array, 0, result, 0, array.length);
        result[array.length] = element;
        return result;
    }


    /**
     * Returns a copy of the given array of size 1 greater than the argument.
     * The last value of the array is left transfer the default value.
     *
     * @param array                 The array transfer copy, must not be [code]null } .
     * @param newArrayComponentType If [code]array } is [code]null } , create a
     *                              size 1 array of this type.
     * @return A new copy of the array of size 1 greater than the input.
     */
    private static Object copyArrayGrow1(Object array, Class newArrayComponentType) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0, null)      = [null]
     * ArrayUtils.add(null, 0, "a")       = ["a"]
     * ArrayUtils.add(["a"], 1, null)     = ["a", null]
     * ArrayUtils.add(["a"], 1, "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], 3, "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   (index 小于 0 || index 大于 array.length).
     */
    public static Object[] add(Object[] array, int index, Object element) {
        Class clss;
        if (array != null) {
            clss = array.getClass().getComponentType();
        } else if (element != null) {
            clss = element.getClass();
        } else {
            return new Object[]{null};
        }
        return (Object[]) add(array, index, element, clss);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add(null, 0, true)          = [true]
     * ArrayUtils.add([true], 0, false)       = [false, true]
     * ArrayUtils.add([false], 1, true)       = [false, true]
     * ArrayUtils.add([true, false], 1, true) = [true, true, false]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static boolean[] add(boolean[] array, int index, boolean element) {
        return (boolean[]) add(array, index, ObjectUtil.toBoolean(element), Boolean.TYPE);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add(null, 0, 'a')            = ['a']
     * ArrayUtils.add(['a'], 0, 'b')           = ['b', 'a']
     * ArrayUtils.add(['a', 'b'], 0, 'c')      = ['c', 'a', 'b']
     * ArrayUtils.add(['a', 'b'], 1, 'k')      = ['a', 'k', 'b']
     * ArrayUtils.add(['a', 'b', 'c'], 1, 't') = ['a', 't', 'b', 'c']
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static char[] add(char[] array, int index, char element) {
        return (char[]) add(array, index, element, Character.TYPE);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 3)      = [2, 6, 3]
     * ArrayUtils.add([2, 6], 0, 1)      = [1, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static byte[] add(byte[] array, int index, byte element) {
        return (byte[]) add(array, index, element, Byte.TYPE);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 10)     = [2, 6, 10]
     * ArrayUtils.add([2, 6], 0, -4)     = [-4, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static short[] add(short[] array, int index, short element) {
        return (short[]) add(array, index, element, Short.TYPE);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 10)     = [2, 6, 10]
     * ArrayUtils.add([2, 6], 0, -4)     = [-4, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static int[] add(int[] array, int index, int element) {
        return (int[]) add(array, index, element, Integer.TYPE);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * ArrayUtils.add([1L], 0, 2L)           = [2L, 1L]
     * ArrayUtils.add([2L, 6L], 2, 10L)      = [2L, 6L, 10L]
     * ArrayUtils.add([2L, 6L], 0, -4L)      = [-4L, 2L, 6L]
     * ArrayUtils.add([2L, 6L, 3L], 2, 1L)   = [2L, 6L, 1L, 3L]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static long[] add(long[] array, int index, long element) {
        return (long[]) add(array, index, element, Long.TYPE);
    }

    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add([1.1f], 0, 2.2f)               = [2.2f, 1.1f]
     * ArrayUtils.add([2.3f, 6.4f], 2, 10.5f)        = [2.3f, 6.4f, 10.5f]
     * ArrayUtils.add([2.6f, 6.7f], 0, -4.8f)        = [-4.8f, 2.6f, 6.7f]
     * ArrayUtils.add([2.9f, 6.0f, 0.3f], 2, 1.0f)   = [2.9f, 6.0f, 1.0f, 0.3f]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static float[] add(float[] array, int index, float element) {
        return (float[]) add(array, index, element, Float.TYPE);
    }


    /**
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements transfer the right (adds one transfer their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , a new one element array is returned
     * whose component type is the same as the element.
     * <p>
     * ArrayUtils.add([1.1], 0, 2.2)              = [2.2, 1.1]
     * ArrayUtils.add([2.3, 6.4], 2, 10.5)        = [2.3, 6.4, 10.5]
     * ArrayUtils.add([2.6, 6.7], 0, -4.8)        = [-4.8, 2.6, 6.7]
     * ArrayUtils.add([2.9, 6.0, 0.3], 2, 1.0)    = [2.9, 6.0, 1.0, 0.3]
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public static double[] add(double[] array, int index, double element) {
        return (double[]) add(array, index, element, Double.TYPE);
    }

    /**
     * Underlying implementation of add(array, index, element) methods.
     * The last parameter is the class, which may not equal element.getClass
     * for primitives.
     *
     * @param array   the array transfer add the element transfer, may be [code]null [/code]
     * @param index   the position of the new object
     * @param element the object transfer add
     * @param clss    the type of the element being added
     * @return A new array containing the existing elements and the new element
     */
    private static Object add(Object array, int index, Object element, Class clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(clss, length + 1);
        arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns the length of the specified array.
     * This method can deal with [code]Object } arrays and with primitive arrays.
     * <p>
     * If the input array is [code]null } , [code]0 } is returned.
     * <p>
     * ArrayUtils.getLength(null)            = 0
     * ArrayUtils.getLength([])              = 0
     * ArrayUtils.getLength([null])          = 1
     * ArrayUtils.getLength([true, false])   = 2
     * ArrayUtils.getLength([1, 2, 3])       = 3
     * ArrayUtils.getLength(["a", "b", "c"]) = 3
     *
     * @param array the array transfer retrieve the length from, may be null
     * @return The length of the array, or [code]0 } if the array is [code]null [/code]
     * @throws IllegalArgumentException if the object arguement is not an array.
     * @since 2.1
     */
    public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * Checks whether two arrays are the same type taking into account
     * multi-dimensional arrays.
     *
     * @param array1 the first array, must not be [code]null [/code]
     * @param array2 the second array, must not be [code]null [/code]
     * @return [code]true } if type of arrays matches
     * @throws IllegalArgumentException if either array is [code]null [/code]
     */
    public static boolean isSameType(Object array1, Object array2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        return array1.getClass().getName().equals(array2.getClass().getName());
    }

    /**
     * 倒转数组.
     * <p>
     * This method does nothing for a [code]null } input array.
     *
     * @param array the array transfer reverse, may be [code]null [/code]
     */
    public static void reverse(short[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        short tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 倒转数组
     * <p>
     * This method does nothing for a [code]null } input array.
     *
     * @param array the array transfer reverse, may be [code]null [/code]
     */
    public static void reverse(char[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        char tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 倒转数组Reverses the order of the given array.
     * <p>
     * This method does nothing for a [code]null } input array.
     *
     * @param array the array transfer reverse, may be [code]null [/code]
     */
    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 倒转数组Reverses the order of the given array.
     * <p>
     * This method does nothing for a [code]null } input array.
     *
     * @param array the array transfer reverse, may be [code]null [/code]
     */
    public static void reverse(double[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        double tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 倒转数组Reverses the order of the given array.
     * <p>
     * This method does nothing for a [code]null } input array.
     *
     * @param array the array transfer reverse, may be [code]null [/code]
     */
    public static void reverse(float[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        float tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 倒转数组Reverses the order of the given array.
     * <p>
     * This method does nothing for a [code]null } input array.
     *
     * @param array the array transfer reverse, may be [code]null [/code]
     */
    public static void reverse(boolean[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        boolean tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 得到初始化好的int数组。
     *
     * @param length 数组长度
     * @param value  初始值
     * @return 初始化后的int数组，各个元素的值都等于指定的value。
     * @since 0.5
     */
    public static int[] getInitedIntArray(int length, int value) {
        int[] indexes = new int[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = value;
        }
        return indexes;
    }



// IndexOf search
    // ----------------------------------------------------------------------


    /**
     * Finds the index of the given object in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array        the array transfer search through for the object, may be [code]null [/code]
     * @param objectToFind the object transfer find, may be [code]null [/code]
     * @param startIndex   the index transfer start searching at
     * @return the index of the object within the array starting at the index,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given object within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array        the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param objectToFind the object transfer find, may be [code]null [/code]
     * @return the last index of the object within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given object in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than
     * the array length will search from the end of the array.
     *
     * @param array        the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param objectToFind the object transfer find, may be [code]null [/code]
     * @param startIndex   the start index transfer travers backwards from
     * @return the last index of the object within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i >= 0; i--) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the object is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array        the array transfer search through
     * @param objectToFind the object transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
    }

    // long IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(long[] array, long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(long[] array, long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(long[] array, long valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // int IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(int[] array, int valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(int[] array, int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(int[] array, int valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // short IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(short[] array, short valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(short[] array, short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(short[] array, short valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // char IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     * @since 2.1
     */
    public static int indexOf(char[] array, char valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     * @since 2.1
     */
    public static int indexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     * @since 2.1
     */
    public static int lastIndexOf(char[] array, char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     * @since 2.1
     */
    public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     * @since 2.1
     */
    public static boolean contains(char[] array, char valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // byte IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(byte[] array, byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // double IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(double[] array, double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value within a given tolerance in the array.
     * This method will return the index of the first value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param tolerance   tolerance of the search
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * This method will return the index of the first value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @param tolerance   tolerance of the search
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(double[] array, double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value within a given tolerance in the array.
     * This method will return the index of the last value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param tolerance   tolerance of the search
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * This method will return the index of the last value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @param tolerance   search for value within plus/minus this amount
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(double[] array, double valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    /**
     * Checks if a value falling within the given tolerance is in the
     * given array.  If the array contains a value within the inclusive range
     * defined by (value - tolerance) transfer (value + tolerance).
     * <p>
     * The method returns [code]false } if a [code]null } array
     * is passed in.
     *
     * @param array       the array transfer search
     * @param valueToFind the value transfer find
     * @param tolerance   the array contains the tolerance of the search
     * @return true if value falling within tolerance is in array
     */
    public static boolean contains(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance) != INDEX_NOT_FOUND;
    }

    // float IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(float[] array, float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(float[] array, float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than the
     * array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(float[] array, float valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // boolean IndexOf
    //-----------------------------------------------------------------------

    /**
     * Finds the index of the given value in the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int indexOf(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * Finds the index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ).
     *
     * @param array       the array transfer search through for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the index transfer start searching at
     * @return the index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null [/code]
     * array input
     */
    public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index of the given value within the array.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if
     * [code]null } array input.
     *
     * @param array       the array transfer travers backwords looking for the object, may be [code]null [/code]
     * @param valueToFind the object transfer find
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(boolean[] array, boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * Finds the last index of the given value in the array starting at the given index.
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} (  {@code -1 } ) for a [code]null } input array.
     * <p>
     * A negative startIndex will return {@link #INDEX_NOT_FOUND} (  {@code -1 } ). A startIndex larger than
     * the array length will search from the end of the array.
     *
     * @param array       the array transfer traverse for looking for the object, may be [code]null [/code]
     * @param valueToFind the value transfer find
     * @param startIndex  the start index transfer travers backwards from
     * @return the last index of the value within the array,
     * {@link #INDEX_NOT_FOUND} (  {@code -1 } ) if not found or [code]null } array input
     */
    public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (array == null || array.length < 1) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Checks if the value is in the given array.
     * <p>
     * The method returns [code]false } if a [code]null } array is passed in.
     *
     * @param array       the array transfer search through
     * @param valueToFind the value transfer find
     * @return [code]true } if the array contains the object
     */
    public static boolean contains(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove([1], 0)          = []
     * ArrayUtils.remove([1, 0], 0)       = [0]
     * ArrayUtils.remove([1, 0], 1)       = [1]
     * ArrayUtils.remove([1, 0, 1], 1)    = [1, 1]
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static byte[] remove(byte[] array, int index) {
        return (byte[]) remove((Object) array, index);
    }


    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove(['a'], 0)           = []
     * ArrayUtils.remove(['a', 'b'], 0)      = ['b']
     * ArrayUtils.remove(['a', 'b'], 1)      = ['a']
     * ArrayUtils.remove(['a', 'b', 'c'], 1) = ['a', 'c']
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static char[] remove(char[] array, int index) {
        return (char[]) remove((Object) array, index);
    }


    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove([1.1], 0)           = []
     * ArrayUtils.remove([2.5, 6.0], 0)      = [6.0]
     * ArrayUtils.remove([2.5, 6.0], 1)      = [2.5]
     * ArrayUtils.remove([2.5, 6.0, 3.8], 1) = [2.5, 3.8]
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static double[] remove(double[] array, int index) {
        return (double[]) remove((Object) array, index);
    }


    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove([1.1], 0)           = []
     * ArrayUtils.remove([2.5, 6.0], 0)      = [6.0]
     * ArrayUtils.remove([2.5, 6.0], 1)      = [2.5]
     * ArrayUtils.remove([2.5, 6.0, 3.8], 1) = [2.5, 3.8]
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static float[] remove(float[] array, int index) {
        return (float[]) remove((Object) array, index);
    }


    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static int[] remove(int[] array, int index) {
        return (int[]) remove((Object) array, index);
    }


    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static long[] remove(long[] array, int index) {
        return (long[]) remove((Object) array, index);
    }

    public static String[] remove(String[] array, String element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return array;
        }
        return remove(array, index);
    }

    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    public static short[] remove(short[] array, int index) {
        return (short[]) remove((Object) array, index);
    }


    /**
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted transfer the left (substracts one from
     * their indices).
     * <p>
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     * <p>
     * If the input array is [code]null } , an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * @param array the array transfer remove the element from, may not be [code]null [/code]
     * @param index the position of the element transfer be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 2.1
     */
    private static Object remove(Object array, int index) {
        int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    private static String[] remove(String[] array, int index) {
        int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        String[] result = new String[length - 1];
        arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }

    /**
     * 得到初始化好的boolean数组。
     *
     * @param length 数组长度
     * @param value  初始值
     * @return 初始化后的boolean数组，各个元素的值都等于value。
     * @since 0.5
     */
    public static boolean[] getInitBooleanArray(int length, boolean value) {
        boolean[] indexes = new boolean[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = value;
        }
        return indexes;
    }

    /**
     * 得到指定的对象在对象数组中的索引。
     *
     * @param objects 对象数组
     * @param object  对象
     * @return 对象在对象数组中的位置，不存在于数组中时返回-1
     * @since 0.5
     */
    public static int indexOf(Object[] objects, Object object) {
        if (objects == null) {
            return -1;
        }
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].equals(object)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将原数组的值拷贝到目标数组。
     * 目标数组的大小必须大于等于原数组，一般应该是大小相等，如果目标数组的大小较大则大于的部分保留原值。
     *
     * @param orginalArray 原数组
     * @param targetArray  目标数组
     * @since 0.5
     */
    public static void copyArrayValue(int[] orginalArray, int[] targetArray) {
        arraycopy(orginalArray, 0, targetArray, 0, orginalArray.length);
    }

    /**
     * 将数组中的值移位。
     * 移动的方式是将指定位置以后的每个元素依次往前移动一位，指定位置的值移到最后。
     *
     * @param array 数组
     * @param index 位置
     */
    public static void shiftArray(int[] array, int index) {
        int temp = array[index];
        int length = array.length - 1;
        arraycopy(array, index + 1, array, index, length - index);
        array[length] = temp;
    }


    /**
     * @param array 数组
     * @param index 位置
     * @param value 值
     * @return 将指定的值插入到数组中的指定位置。数组最后一个元素的值将被丢弃。
     */
    public static int[] insertValueToArray(int[] array, int index, int value) {
        if (array == null) {
            int[] resultarray = new int[index + 1];
            resultarray[index] = value;
            return resultarray;
        }
        int length = array.length - 1;
        System.arraycopy(array, index, array, index + 1, length - index);
        array[index] = value;
        return array;
    }

    /**
     * 数组内容交换位置
     *
     * @param array  数组
     * @param index1 交换位置1
     * @param index2 交换位置2
     */
    public static void swap(String[] array, int index1, int index2) {
        String stmp = array[index1];
        array[index1] = array[index2];
        array[index2] = stmp;
        //return array;
    }

    /**
     * 将字符串数组使用指定的变为 SQL in ('1','2','3') 格式
     *
     * @param array 字符串数组
     * @return 合并后的字符串
     * @since 0.4
     */
    public static String toSqlString(String[] array) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder result = new StringBuilder();
        for (String anArray : array) {
            result.append("'").append(anArray).append("',");
        }
        if (result.length() <= 1) {
            return StringUtil.empty;
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * @param array1     数组1
     * @param array2     数组2
     * @param ignoreCase 是否区分大小写
     * @return String[]   得到数组的交集          2,3,5,6,7, 1,2,3,4
     */
    public static String[] intersection(String[] array1, String[] array2, boolean ignoreCase) {
        if (array1 == null) {
            return new String[0];
        }
        if (array2 == null) {
            return new String[0];
        }
        String[] result = null;
        for (String anArray : array2) {
            if (inArray(array1, anArray, ignoreCase)) {
                result = add(result, anArray);
            }
        }
        if (result == null) {
            return new String[0];
        }
        return result;
    }

    /**
     * @param setA       数组1   大数主
     * @param setB       数组2
     * @param ignoreCase 不区分大小写
     * @return 求差集
     */
    public static String[] difference(String[] setA, String[] setB, boolean ignoreCase) {
        if (setA == null) {
            return setB;
        }
        if (setB == null) {
            return setA;
        }
        String[] result = null;
        for (String b : setB) {
            if (b == null) {
                continue;
            }
            if (!inArray(setA, b, ignoreCase)) {
                result = add(result, b);
            }
        }

        return result;
    }

    /**
     * @param a 数组a
     * @param b 数组b
     * @return 集合相减  a-b
     */
    public static String[] subtract(String[] a, String[] b) {
        if (a == null) {
            return null;
        }
        if (b == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, a);
        for (String x : b) {
            list.remove(x);
        }
        String[] result = new String[list.size()];
        return list.toArray(result);
    }

    /**
     * @param a 数组a
     * @param b 数组b
     * @return 集合相减  a-b
     */
    public static Class<?>[] subtract(Class<?>[] a, Class<?>[] b) {
        if (a == null) {
            return null;
        }
        if (b == null) {
            return null;
        }
        List<Class<?>> list = new ArrayList<>();
        Collections.addAll(list, a);
        for (Class<?> x : b) {
            list.remove(x);
        }
        Class<?>[] result = new Class[list.size()];
        return list.toArray(result);
    }
    /**
     * @param array      数组
     * @param find       查找字符串
     * @param ignoreCase 大小写
     * @return 判断是否在数组中
     */
    public static boolean inArray(String[] array, String find, boolean ignoreCase) {
        if (array == null) {
            return false;
        }
        if (!StringUtil.hasLength(find)) {
            return false;
        }
        for (String anArray : array) {
            if (ignoreCase) {
                if (find.equalsIgnoreCase(anArray)) {
                    return true;
                }
            } else if (find.equals(anArray)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param array 字符串
     * @param delim 切分
     * @param dec   排序方式
     * @return String[] 按照 数组长度排序
     */
    public static String[] sortFind(String[] array, String delim, boolean dec) {
        String stmp;
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length - 1; j >= i; j--) {
                if (dec) {
                    if (StringUtil.countMatches(array[i], delim) > StringUtil.countMatches(array[j], delim)) {
                        stmp = array[j];
                        array[j] = array[i];
                        array[i] = stmp;
                    }
                } else {
                    if (StringUtil.countMatches(array[i], delim) < StringUtil.countMatches(array[j], delim)) {
                        stmp = array[j];
                        array[j] = array[i];
                        array[i] = stmp;
                    }
                }
            }
        }
        return array;
    }


    /**
     * 金字塔替换
     *
     * @param array    数组
     * @param arrayFen 分工符
     * @param replace  替换为表示符
     */
    public static void replace(String[] array, String arrayFen, String replace) {
        for (int i = array.length - 1; i > 0; i--) {
            String[] rep = StringUtil.split(array[i - 1], arrayFen);
            if (rep.length <= 0) {
                continue;
            }
            for (String aRep : rep) {
                if (array[i] == null) {
                    continue;
                }
                if (!array[i].endsWith(aRep + arrayFen)) {
                    array[i] = StringUtil.replace(array[i], aRep + arrayFen, replace);
                } else {
                    if (array[i].length() > (aRep + arrayFen).length()) {
                        array[i] = array[i].substring(0, array[i].length() - (aRep + arrayFen).length());
                        array[i] = StringUtil.replace(array[i], aRep + arrayFen, replace);
                        array[i] = array[i] + aRep;
                    }
                }
            }

        }
        //return array;
    }


    /**
     * @param array      数组
     * @param remove     要删除的
     * @param ignoreCase 大小写
     * @return String[] 删除数组中内容
     */
    public static String[] delete(String[] array, String remove, boolean ignoreCase) {
        if (remove == null) {
            remove = StringUtil.empty;
        }
        if (array == null || array.length == 0) {
            return new String[0];
        }
        String[] result = null;
        for (String anArray : array) {
            if (ignoreCase && !remove.equalsIgnoreCase(anArray)) {
                result = add(result, anArray);
            } else if (!ignoreCase && !remove.equals(anArray)) {
                result = add(result, anArray);
            }
        }
        return result;
    }

    /**
     * @param array  数组
     * @param remove 删除对象
     * @return 数组中删除一个数
     */
    public static int[] delete(int[] array, int remove) {
        if (array == null) {
            return new int[0];
        }
        int[] result = null;
        for (int anArray : array) {
            if (anArray != remove) {
                result = add(result, anArray);
            }
        }
        return result;
    }


    /**
     * @param array      数组
     * @param deleteNull 删除null
     * @return String[] 删除数组中重复的内容
     */
    public static String[] deleteRepeated(String[] array, boolean deleteNull) {
        if (array == null) {
            return new String[0];
        }
        String[] result = null;
        for (String anArray : array) {
            if (!inArray(result, anArray, false)) {
                if (deleteNull && StringUtil.isEmpty(anArray)) {
                    continue;
                }
                result = add(result, anArray);
            }
        }
        if (result == null) {
            return new String[0];
        }
        return result;
    }

    /**
     * @param array  数组1
     * @param append 数组2
     * @return String[]  追加数组 内容
     */
    public static String[] join(String[] array, String[] append) {
        if (array == null) {
            return append;
        }
        if (append == null) {
            return array;
        }
        int length = array.length + append.length;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            if (i < array.length) {
                result[i] = array[i];
            } else {
                result[i] = append[i - array.length];
            }
        }
        return result;
    }

    public static String toString(String[] array, String fen1, String fen2) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (String anArray : array) {
            sb.append(fen1).append(anArray).append(fen2);
        }
        return sb.toString();
    }

    public static String toString(Object[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    public static String toString(String[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    public static String toString(boolean[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    public static String toString(Integer[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    public static String toString(int[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    public static String toString(long[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    public static String toString(Long[] array, String fen) {
        if (array == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    /**
     * 判断两个数组中的内容是否一样
     *
     * @param arr1 数组1
     * @param arr2 数组2
     * @return boolean 判断两个数组中的内容是否一样
     */
    public static boolean equals(String[] arr1, String[] arr2) {
        return equals(arr1, arr2, false);
    }

    /**
     * @param arr1       数组1
     * @param arr2       数组2
     * @param ignoreCase 大小写
     * @return boolean 判断两个数组中的内容是否一样
     */
    public static boolean equals(String[] arr1, String[] arr2, boolean ignoreCase) {
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1 == null) {
            return false;
        }
        if (arr2 == null) {
            return false;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (ignoreCase && !arr1[i].equalsIgnoreCase(arr2[i])) {
                return false;
            } else if (!arr1[i].equals(arr2[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param array      数组
     * @param stmp       条件
     * @param ignorecase 大小写
     * @return String[] 去除array 中包含 stmp 的隔子
     */
    public static String[] trim(String[] array, String stmp, boolean ignorecase) {
        if (array == null) {
            return new String[1];
        }
        List<String> list = new ArrayList<String>();
        if (stmp == null) {
            for (String anArray : array) {
                if (anArray != null) {
                    list.add(anArray);
                }
            }
            return ListUtil.toArray(list);
        } else {
            for (String anArray : array) {
                if (!ignorecase) {
                    if (!stmp.equals(anArray)) {
                        list.add(anArray);
                    } else if (!stmp.equalsIgnoreCase(anArray)) {
                        list.add(anArray);
                    }
                }
            }
            return ListUtil.toArray(list);
        }
    }

    /**
     * @param array    数组
     * @param arrayFen 数组分割
     * @param replace  替换
     * @return String[]  Xtree分组，排序
     */
    public static String[] sortTreeArray(String[] array, String arrayFen, String replace) {
        if (array == null) {
            return new String[1];
        }
        //保存最短的数据
        String[] minArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                continue;
            }
            String sfind = StringUtil.substringBefore(array[i], arrayFen) + arrayFen;
            if (!inArray(minArray, sfind, false)) {
                minArray[i] = StringUtil.substringBefore(array[i], arrayFen) + arrayFen;
            }
        }
        minArray = trim(minArray, StringUtil.NULL, false);
        //保存已经分配的列表
        String[] minfen = new String[0];
        //返回数据
        String[] result = new String[0];
        //找出相同的
        for (String minString : minArray) {
            //得到主分类，的附属
            String[] tempList = new String[1];
            tempList[0] = minString;
            for (String stmp : array) {
                if (StringUtil.countMatches(stmp, minString) > 0) {
                    if (stmp != null && !inArray(tempList, stmp, false)) {
                        tempList = add(tempList, stmp);
                    }
                }
            }
            String[] reparray = sortFind(tempList, arrayFen, true);
            minfen = join(minfen, reparray);
            //替换处理
            if (replace != null) {
                replace(reparray, arrayFen, replace);
            }
            result = join(result, reparray);
        }
        /**
         * 处理无主分类的部分,是 array 中还没有排序的 在 minfen中没有的部分
         */
        String[] lastArray = new String[0];
        for (String anArray : array) {
            if (anArray == null) {
                continue;
            }
            if (!inArray(minfen, anArray, false)) {
                lastArray = add(lastArray, anArray);
            }
        }
        sortArray(lastArray, arrayFen, true);
        if (replace != null) {
            replace(lastArray, arrayFen, replace);
        }
        return join(result, lastArray);
    }


    /**
     * 更具支持出现的次数排序
     *
     * @param array 数组
     * @param delim 出现的字符串
     * @param dec   升降
     */
    public static void sortArray(String[] array, String delim, boolean dec) {
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length - 1; j >= i; j--) {
                String[] temparray = StringUtil.split(array[i], delim);
                for (String aTemparray : temparray) {
                    if (StringUtil.countMatches(array[j], aTemparray) > 0) {
                        swap(array, i, j);
                        if (i > 1) {
                            if (dec) {
                                if (StringUtil.countMatches(array[i - 1], delim) > StringUtil.countMatches(array[i], delim)) {
                                    swap(array, i, i - 1);
                                }
                            } else {
                                if (StringUtil.countMatches(array[i - 1], delim) < StringUtil.countMatches(array[i], delim)) {
                                    swap(array, i, i - 1);
                                }

                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 对字符串中出现的数字来排序
     *
     * @param array 数组
     */
    public static void sort(String[] array) {
        if (isEmpty(array)) {
            return;
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length - 1; j >= i; j--) {
                if (StringUtil.toInt(StringUtil.getNumber(array[i])) > StringUtil.toInt(StringUtil.getNumber(array[j]))) {
                    swap(array, i, j);
                }
            }
        }
    }

    /**
     * 对字符串中某个字符出现的次数排序
     *
     * @param array 数组
     * @param delim 字符
     * @param dec   true 少的排上边,false:少的排下边
     */
    public static void sort(String[] array, String delim, boolean dec) {
        if (isEmpty(array)) {
            return;
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length - 1; j >= i; j--) {
                if (dec ? StringUtil.countMatches(array[i], delim) > StringUtil.countMatches(array[j], delim) : StringUtil.countMatches(array[i], delim) < StringUtil.countMatches(array[j], delim)) {
                    swap(array, i, j);
                }
            }
        }
    }

    /**
     * @param array 数字内容的字符串
     * @return 字符串转换为 int
     */
    public static int[] getIntArray(String[] array) {
        if (array == null) {
            return null;
        }
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = StringUtil.toInt(array[i]);
        }
        return intArray;
    }

    public static Integer[] getIntegerArray(String[] array) {
        if (array == null) {
            return null;
        }
        Integer[] intArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = StringUtil.toInt(array[i]);
        }
        return intArray;
    }

    public static Integer[] getIntegerArray(int[] array) {
        if (array == null) {
            return null;
        }
        Integer[] intArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = array[i];
        }
        return intArray;
    }

    public static long[] getLongArray(String[] array) {
        if (array == null) {
            return null;
        }
        long[] longArray = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            longArray[i] = StringUtil.toLong(array[i]);
        }
        return longArray;
    }

    public static Long[] getLongObjectArray(long[] array) {
        if (array == null) {
            return null;
        }
        Long[] longArray = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            longArray[i] = Long.valueOf(array[i]);
        }
        return longArray;
    }

    /**
     * @param array 数字内容的字符串
     * @return 字符串转换为Long
     */
    public static Long[] getLongObjectArray(String[] array) {
        if (array == null) {
            return null;
        }
        Long[] longArray = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            longArray[i] = StringUtil.toLong(array[i]);
        }
        return longArray;
    }


    /**
     * @param array 数字内容的字符串
     * @return 字符串转换为float
     */
    public static float[] getFloatArray(String[] array) {
        if (array == null) {
            return null;
        }
        float[] floatArray = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            floatArray[i] = StringUtil.toFloat(array[i]);
        }
        return floatArray;
    }

    public static Float[] getFloatObjectArray(float[] array) {
        if (array == null) {
            return null;
        }
        Float[] floatArray = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            floatArray[i] = array[i];
        }
        return floatArray;
    }

    public static Float[] getFloatObjectArray(String[] array) {
        if (array == null) {
            return null;
        }
        Float[] floatArray = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            floatArray[i] = StringUtil.toFloat(array[i]);
        }
        return floatArray;
    }

    /**
     * @param array 数字内容的字符串
     * @return 字符串转换为double
     */
    public static double[] getDoubleArray(String[] array) {
        if (array == null) {
            return null;
        }
        double[] doubleArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] = StringUtil.toDouble(array[i]);
        }
        return doubleArray;
    }

    public static Double[] getDoubleObjectArray(double[] array) {
        if (array == null) {
            return null;
        }
        Double[] doubleArray = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] = array[i];
        }
        return doubleArray;
    }

    public static Double[] getDoubleObjectArray(String[] array) {
        if (array == null) {
            return null;
        }
        Double[] doubleArray = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] = StringUtil.toDouble(array[i]);
        }
        return doubleArray;
    }

    public static BigDecimal[] getBigDecimalArray(Double[] array) {
        if (array == null) {
            return null;
        }
        BigDecimal[] bigDecimalArray = new BigDecimal[array.length];
        for (int i = 0; i < array.length; i++) {
            bigDecimalArray[i] = BigDecimal.valueOf(array[i]);
        }
        return bigDecimalArray;
    }

    public static BigDecimal[] getBigDecimalArray(String[] array) {
        if (array == null) {
            return null;
        }
        BigDecimal[] bigDecimalArray = new BigDecimal[array.length];
        for (int i = 0; i < array.length; i++) {
            bigDecimalArray[i] = BigDecimal.valueOf(StringUtil.toDouble(array[i]));
        }
        return bigDecimalArray;
    }


    public static String[] toStringArray(Object[] array) {
        if (array == null) {
            return null;
        }
        String[] doubleArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] = array[i].toString();
        }
        return doubleArray;
    }

    public static Integer[] toIntegerArray(Object[] array) {
        if (array == null) {
            return null;
        }
        Integer[] doubleArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] =  ObjectUtil.toInt(array[i]) ;
        }
        return doubleArray;
    }

    public static Float[] toFloatArray(Object[] array) {
        if (array == null) {
            return null;
        }
        Float[] doubleArray = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] =  ObjectUtil.toFloat(array[i]) ;
        }
        return doubleArray;
    }

    public static Double[] toDoubleArray(Object[] array) {
        if (array == null) {
            return null;
        }
        Double[] doubleArray = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] =  ObjectUtil.toDouble(array[i]) ;
        }
        return doubleArray;
    }
    public static Object[] toArray(int[] array) {
        if (array == null) {
            return new Object[0];
        }
        Object[] oArray = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            oArray[i] = array[i];
        }
        return oArray;
    }


    /**
     * @param fields 字段
     * @param terms  条件
     * @param type   类型
     * @param havAnd 添加 and
     * @return String 生成SQL查询条件
     */
    public static String getFieldTerms(String[] fields, String[] terms, String type, boolean havAnd) {
        if (StringUtil.isNull(type)) {
            type = "like";
        }
        if (StringUtil.isNull(type)) {
            type = "=";
        }
        if (fields == null || fields.length < 1) {
            return StringUtil.empty;
        }
        if (terms == null || terms.length < 1) {
            return StringUtil.empty;
        }
        int len = NumberUtil.getMin(new int[]{fields.length, terms.length});
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if ("like".equalsIgnoreCase(type)) {
                if (StringUtil.isNull(fields[i])) {
                    continue;
                }
                sb.append("AND ").append(StringUtil.checkSql(fields[i])).append(" LIKE '%").append(StringUtil.checkSql(terms[i])).append("%' ");
            } else if ("=".equalsIgnoreCase(type)) {
                sb.append("AND ").append(StringUtil.checkSql(fields[i])).append("='").append(StringUtil.checkSql(terms[i])).append("' ");
            }
        }
        if (!havAnd) {
            if (sb.length() > 2) {
                return sb.substring(3);
            }
            return StringUtil.empty;
        }
        return sb.toString();
    }

    /**
     * @param error 打印输出错误信息
     * @return 字符串
     */
    public static String getHtmlError(String[] error) {
        if (error == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (String er : error) {
            sb.append(er).append("</br>");
        }
        return sb.toString();
    }

    /**
     * @param objects 对象数组
     * @return 判断是否有空对象
     */
    public static int indexOfNull(Object[] objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                return i;
            }
        }
        return -1;
    }


    /**
     * @param ary 排序
     * @return 冒泡排序
     */
    public static int[] sort(int[] ary) {
        int count = 0;
        for (int i = 0; i < ary.length - 1; i++) {
            boolean tap = false;
            for (int j = 0; j < ary.length - i - 1; j++) {
                if (ary[j] > ary[j + 1]) {//相邻两数进行比较，大的就移动到后面去！
                    int temp = ary[j];
                    ary[j] = ary[j + 1];
                    ary[j + 1] = temp;
                    tap = true;
                }
                count++;//计算运算了多少次！
            }
            if (!tap) {
                break;//如果j 与  j+1没有交换，跳出循环！
            }
        }
        return ary;
    }


    /**
     * 2203;2218;2221;2222;2223;2224;2225;2228;2229;2231;2233;2235;2236;2237;2238;2239;2240;2241;2242;2244;2245;2246;2248;2249;2252;2254;2256;2257;2258;2262
     * int[] array = new int[]{2,3,4,5,6,7,9,10,20,30,40,41,42,43,50};
     *
     * @param array 数组
     * @return 转字符串
     */
    public static String getArrayExpression(int[] array) {
        array = sort(array);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);

            for (int j = i + 1; j < array.length; j++) {
                if (array[j - 1] == array[j] - 1) {
                    if (array.length == j + 1) {
                        sb.append("-").append(array[j]);
                        i = j;
                        break;
                    }
                } else {
                    if (j - i > 1) {
                        sb.append("-").append(array[j - 1]).append(StringUtil.SEMICOLON);
                        i = j - 1;
                    } else {
                        sb.append(StringUtil.SEMICOLON);
                        i = j - 1;
                    }
                    break;
                }
            }
        }
        if (sb.toString().endsWith(StringUtil.SEMICOLON)) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    // Subarrays
    //-----------------------------------------------------------------------

    /**
     * Produces a new array containing the elements between
     * the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     * <p>
     * The component type of the subarray is always the same as
     * that of the input array. Thus, if the input is an array of type
     * [code]Date } , the following usage is envisaged:
     *
     * <pre>
     * Date[] someDates = (Date[])ArrayUtils.subarray(allDates, 2, 5);
     * </pre>
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static Object[] subArray(Object[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            return (Object[]) Array.newInstance(type, 0);
        }
        Object[] subarray = (Object[]) Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]long } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static long[] subArray(long[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new long[0];
        }

        long[] subarray = new long[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]int } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static int[] subArray(int[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new int[0];
        }

        int[] subarray = new int[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]short } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static short[] subArray(short[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new short[0];
        }

        short[] subarray = new short[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]char } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static char[] subArray(char[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new char[0];
        }

        char[] subarray = new char[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]byte } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static byte[] subArray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new byte[0];
        }

        byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]double } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static double[] subArray(double[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new double[0];
        }

        double[] subarray = new double[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]float } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static float[] subArray(float[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new float[0];
        }

        float[] subarray = new float[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * Produces a new [code]boolean } array containing the elements
     * between the start and end indices.
     * <p>
     * The start index is inclusive, the end index exclusive.
     * Null array input produces null output.
     *
     * @param array               the array
     * @param startIndexInclusive the starting index. Undervalue (&lt;0)
     *                            is promoted transfer 0, overvalue (&gt;array.length) results
     *                            in an empty array.
     * @param endIndexExclusive   elements up transfer endIndex-1 are present in the
     *                            returned subarray. Undervalue (&lt; startIndex) produces
     *                            empty array, overvalue (&gt;array.length) is demoted transfer
     *                            array length.
     * @return a new array containing the elements between
     * the start and end indices.
     * @since 2.1
     */
    public static boolean[] subArray(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return new boolean[0];
        }

        boolean[] subarray = new boolean[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static int sum(int[] array) {
        int result = 0;
        for (int anArray : array) {
            result = result + anArray;
        }
        return result;
    }

    /**
     * @param array 数组
     * @return 得到最大的索引好
     */
    public static int maxIndex(int[] array) {
        int result = 0;
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > result) {
                result = array[i];
                index = i;
            }
        }
        return index;
    }

    public static int max(int[] array) {
        int result = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > result) {
                result = array[i];
            }
        }
        return result;
    }

    public static int min(int[] array) {
        int result = max(array);
        for (int i = 0; i < array.length; i++) {
            if (array[i] < result) {
                result = array[i];
            }
        }
        return result;
    }


    /**
     * 字符串返回 Collection
     *
     * @param array 数组
     * @return 字符串返回
     */
    public static Collection<Object> toCollection(Object[] array) {
        Collection<Object> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }




    public static List<Object> toList(Object[] array) {
        List<Object> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        Collections.addAll(list, array);
        return list;
    }


}