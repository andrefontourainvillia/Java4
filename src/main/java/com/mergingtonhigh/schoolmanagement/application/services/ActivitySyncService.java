package com.mergingtonhigh.schoolmanagement.application.services;

import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;

/**
 * Serviço responsável por sincronizar dados embarcados nas atividades
 * quando professores ou categorias são alterados
 */
public interface ActivitySyncService {

    /**
     * Atualiza todas as atividades que contêm referência para um professor
     * específico
     * 
     * @param teacher Professor atualizado
     */
    void syncTeacherDataInActivities(Teacher teacher);

    /**
     * Atualiza todas as atividades que contêm referência para uma categoria
     * específica
     * 
     * @param category Categoria atualizada
     */
    void syncCategoryDataInActivities(ActivityCategory category);

    /**
     * Remove todas as referências embarcadas de um professor das atividades
     * 
     * @param teacherUsername Username do professor a ser removido
     */
    void removeTeacherFromActivities(String teacherUsername);

    /**
     * Remove todas as referências embarcadas de uma categoria das atividades
     * 
     * @param categoryId ID da categoria a ser removida
     */
    void removeCategoryFromActivities(String categoryId);

    /**
     * Sincroniza dados embarcados de uma atividade específica
     * 
     * @param activityName Nome da atividade para sincronizar
     */
    void syncActivityEmbeddedData(String activityName);

    /**
     * Força sincronização completa de todas as atividades
     */
    void syncAllActivities();
}
