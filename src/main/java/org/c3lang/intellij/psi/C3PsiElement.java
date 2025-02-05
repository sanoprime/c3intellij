package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface C3PsiElement extends PsiElement {

    default @NotNull C3ImportProvider getImportProvider() {
        return Objects.requireNonNull(PsiTreeUtil.getParentOfType(this, C3ImportProvider.class));
    }
}
