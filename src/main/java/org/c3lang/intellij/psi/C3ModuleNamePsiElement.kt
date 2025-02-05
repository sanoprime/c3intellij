package org.c3lang.intellij.psi

import com.intellij.psi.util.parentOfType

interface C3ModuleNamePsiElement : C3PsiElement {
    val moduleName: ModuleName?

    fun isSameModule(other: C3FullyQualifiedNamePsiElement): Boolean {
        return this.containingFile.name == other.containingFile.name && moduleName == other.moduleName
    }

    fun isImported(other: C3FullyQualifiedNamePsiElement): Boolean {
        val importProvider = checkNotNull(parentOfType<C3ImportProvider>(true))

        return importProvider.imports.contains(other.moduleName)
    }

}