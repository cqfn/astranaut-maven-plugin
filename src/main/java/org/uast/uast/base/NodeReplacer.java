/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.uast.uast.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator of a new tree with a modified subtree.
 *
 * @since 1.0
 */
public class NodeReplacer {
    /**
     * Replaces a subtree of the initial tree with the newly created subtree.
     * @param tree The initial tree
     * @param source The subtree to be replaced
     * @param target The new subtree
     * @return Modified tree or an empty tree if it was not modified,
     *  and the index:
     *  - {@code -1} if the full tree was replaced or was not replaced at all;
     *  - index of the root child which branch was modified if a subtree was replaced
     */
    public Pair<Node, Integer> replace(final Node tree, final Node source, final Node target) {
        Pair<Node, Integer> result = new Pair<>(EmptyTree.INSTANCE, -1);
        if (tree.equals(source)) {
            result = new Pair<>(target, -1);
        } else if (tree.getChildCount() > 0) {
            final List<Node> list = new ArrayList<>(tree.getChildCount());
            int pos = -1;
            for (int idx = 0; idx < tree.getChildCount(); idx += 1) {
                final Node child = tree.getChild(idx);
                final Pair<Node, Integer> replacement = this.replace(child, source, target);
                if (replacement.getKey().equals(EmptyTree.INSTANCE)) {
                    list.add(child);
                } else {
                    list.add(replacement.getKey());
                    pos = idx;
                }
            }
            if (pos != -1) {
                final Builder builder = tree.getType().createBuilder();
                builder.setChildrenList(list);
                result = new Pair<>(builder.createNode(), pos);
            }
        }
        return result;
    }
}
